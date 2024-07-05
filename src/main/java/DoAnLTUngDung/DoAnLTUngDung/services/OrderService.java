package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.Order;
import DoAnLTUngDung.DoAnLTUngDung.entity.OrderDetail;
import DoAnLTUngDung.DoAnLTUngDung.entity.CartItem;
import DoAnLTUngDung.DoAnLTUngDung.repository.IOrderRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class OrderService {
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IOrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;
    @Transactional
    public Order createOrder(String TenNguoiNhan, String diachi, String sdt,String email,String note,String pttt, List<CartItem> cartItems) {
        Order order = new Order();
        order.setTenNguoiNhan(TenNguoiNhan);
        order.setDiachi(diachi);
        order.setSdt(sdt);
        order.setEmail(email);
        order.setNote(note);
        order.setPttt(pttt);
        order = orderRepository.save(order);
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }
        cartService.clearCart();
        return order;
    }
}
