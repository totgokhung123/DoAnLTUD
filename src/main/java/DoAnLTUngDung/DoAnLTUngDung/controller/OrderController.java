package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.dto.OrderForm;
import DoAnLTUngDung.DoAnLTUngDung.entity.*;
import DoAnLTUngDung.DoAnLTUngDung.repository.IHoaDonRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IOrderRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.OrderDetailRepository;
import DoAnLTUngDung.DoAnLTUngDung.services.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IHoaDonRepository hoaDonRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;

    @Autowired
    private VnpayService vnpayService;

    @Autowired
    private UserServices userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductServices productService;

    @PostMapping("/checkout")
    public String checkout(@RequestParam("selectedProducts") List<Long> selectedProductIds,Model model, HttpSession session,Authentication authentication) {
        // Log để kiểm tra giá trị của selectedProducts
        if (selectedProductIds == null) {
            System.out.println("selectedProducts is null");
        } else {
            System.out.println("selectedProducts size: " + selectedProductIds.size());
            System.out.println("selectedProducts content: " + selectedProductIds);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);

        // Lấy danh sách CartItem được chọn dựa trên user hiện tại
        List<CartItem> selectedCartItems = cartService.findCartItemsByIds(selectedProductIds, user);
        session.setAttribute("selectedCartItems", selectedCartItems);
        // Đưa selectedCartItems vào model để sử dụng trong template
        model.addAttribute("selectedCartItemss", selectedCartItems);
        if (selectedCartItems == null) {
            System.out.println("selectedCartItems is null");
        } else {
            System.out.println("selectedCartItems size: " + selectedCartItems.size());
            System.out.println("selectedCartItems content: " + selectedCartItems);
            for(CartItem cart : selectedCartItems){
                System.out.println("cart id: " + cart.getId());
                System.out.println("cart product name: " + cart.getProduct().getTitle());
                System.out.println("cart product price: " + cart.getProduct().getPrice());
                System.out.println("cart user: " + cart.getUser().getId());
            }
        }
        return "USER/Order";
    }

    @PostMapping("/complete")
    public String submitOrder(
        @RequestParam("recipientName") String recipientName,
        @RequestParam("recipientPhone") String recipientPhone,
        @RequestParam("recipientAddress") String recipientAddress,
        @RequestParam("email") String email,
        @RequestParam("note") String note,
        @RequestParam("paymentMethod") String paymentMethod,
        Model model,
        HttpSession session,
        Authentication authentication,RedirectAttributes redirectAttributes) {
        List<CartItem> selectedCartItems = (List<CartItem>) session.getAttribute("selectedCartItems");
        if (selectedCartItems == null || selectedCartItems.isEmpty()) {
            return "redirect:/cart";
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
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
        if ("VNPAY".equals(paymentMethod)) {
            double amount = totalPrice; // Số tiền cần thanh toán
            String paymentUrl = vnpayService.createPaymentUrl(order.getId().toString(), amount);
            saveHoaDon(order, totalPrice);
            //session.setAttribute("order", order);
           // session.setAttribute("totalPrice", totalPrice);
            return "redirect:" + paymentUrl;
        } else {
            // Lưu HoaDon ngay lập tức khi không sử dụng VNPAY
            saveHoaDon(order, totalPrice);
            session.removeAttribute("selectedCartItems");
            redirectAttributes.addFlashAttribute("message", "Đơn hàng của bạn đã được hoàn tất!");
            return "redirect:/";
        }
    }
    private void saveHoaDon(Order order, double totalPrice) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setOrder(order);
        hoaDon.setNgayLap(new Date());
        hoaDon.setTrangThai("hoan thanh");
        hoaDon.setTotalPrice(totalPrice);
        hoaDonRepository.save(hoaDon);
    }

}