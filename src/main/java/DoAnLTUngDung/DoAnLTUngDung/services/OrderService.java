package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.*;
import DoAnLTUngDung.DoAnLTUngDung.repository.IHoaDonRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.OrderDetailRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private CartService cartService;
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IHoaDonRepository hoaDonRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    private OrderService orderService;
    private ProductServices productService;

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }
    public void saveOrderDetail(OrderDetail order) {
        orderDetailRepository.save(order);
    }
    public void saveHoaDon(HoaDon hoaDon) {
        hoaDonRepository.save(hoaDon);
    }
    public void createOrder(String recipientName, String recipientPhone, String recipientAddress, String email, String note, String paymentMethod, User user, List<CartItem> selectedCartItems) {
        // Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setTenNguoiNhan(recipientName);
        order.setSdt(recipientPhone);
        order.setDiachi(recipientAddress);
        order.setEmail(email);
        order.setNote(note);
        order.setPttt(paymentMethod);

        orderRepository.save(order);
        double totalPrice = 0;
        if (selectedCartItems == null) {
            System.out.println("selectedCartItemsnull o trong Service");
        } else {
            System.out.println("selectedCartItems size trong service: " + selectedCartItems.size());
            System.out.println("selectedCartItems content: " + selectedCartItems);
            for(CartItem cart : selectedCartItems){
                System.out.println("cart id: " + cart.getId());
                System.out.println("cart product name: " + cart.getProduct().getTitle());
                System.out.println("cart product price: " + cart.getProduct().getPrice());
                System.out.println("cart user: " + cart.getUser().getId());
            }
        }
        // Tạo OrderDetail từ CartItem được chọn
        for (CartItem cartItem : selectedCartItems) {
            Product product = cartItem.getProduct();

            if (product != null) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setProduct(product);
                orderDetail.setOrder(order);
                orderDetail.setQuantity(cartItem.getQuantity());
                orderDetail.setPrice(product.getPrice() * cartItem.getQuantity());

                orderDetailRepository.save(orderDetail);
               // order.getOrderDetails().add(orderDetail);
                totalPrice += orderDetail.getPrice();
            }
        }

        // Tạo HoaDon
        HoaDon hoaDon = new HoaDon();
        hoaDon.setOrder(order);
        hoaDon.setNgayLap(new Date());
        hoaDon.setTrangThai("hoan thanh");
        hoaDon.setTotalPrice(totalPrice);
        hoaDonRepository.save(hoaDon);
    }
}