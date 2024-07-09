package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.dto.OrderForm;
import DoAnLTUngDung.DoAnLTUngDung.entity.*;
import DoAnLTUngDung.DoAnLTUngDung.services.CartService;
import DoAnLTUngDung.DoAnLTUngDung.services.OrderService;
import DoAnLTUngDung.DoAnLTUngDung.services.ProductServices;
import DoAnLTUngDung.DoAnLTUngDung.services.UserServices;
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
    private CartService cartService;

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
//        @RequestParam("selectedProducts") List<Long> selectedProductIds,
        @RequestParam("recipientName") String recipientName,
        @RequestParam("recipientPhone") String recipientPhone,
        @RequestParam("recipientAddress") String recipientAddress,
        @RequestParam("email") String email,
        @RequestParam("note") String note,
        @RequestParam("paymentMethod") String paymentMethod,
        Model model,
        HttpSession session,
        Authentication authentication) {
        // Lấy danh sách CartItem được chọn từ session
        List<CartItem> selectedCartItems = (List<CartItem>) session.getAttribute("selectedCartItems");

        if (selectedCartItems == null || selectedCartItems.isEmpty()) {
            return "redirect:/cart"; // Nếu không có sản phẩm nào được chọn, quay lại giỏ hàng
        }
        else {
        System.out.println("selectedCartItems size trong complete: " + selectedCartItems.size());
        System.out.println("selectedCartItems content: " + selectedCartItems);
            for(CartItem cart : selectedCartItems){
                System.out.println("cart anh: " + cart.getProduct().getAnhdaidien());
                System.out.println("cart product name: " + cart.getProduct().getTitle());
                System.out.println("cart product price: " + cart.getProduct().getPrice());
                System.out.println("cart quantity: " + cart.getQuantity());
            }
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);

        orderService.createOrder(recipientName, recipientPhone, recipientAddress, email, note, paymentMethod, user, selectedCartItems);

        // Xóa danh sách sản phẩm đã chọn khỏi session sau khi đặt hàng thành công
        session.removeAttribute("selectedCartItems");
    return "redirect:/";
}
}