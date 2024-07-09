package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.*;
import DoAnLTUngDung.DoAnLTUngDung.services.CartService;
import DoAnLTUngDung.DoAnLTUngDung.services.OrderService;
import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
import DoAnLTUngDung.DoAnLTUngDung.services.ProductServices;
import DoAnLTUngDung.DoAnLTUngDung.services.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.thymeleaf.util.NumberUtils.formatCurrency;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserServices userServices;
    @Autowired
    private OrderService orderService;

    @GetMapping
    public String showCart(Model model, @AuthenticationPrincipal User user, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);
        user = users;
        List<CartItem> cartItems = cartService.getCartItems(user);
        model.addAttribute("cartItems", cartItems);

        int totalQuantity = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        model.addAttribute("cartQuantity", totalQuantity);

        String totalFormatted = cartService.getTotalFormatted(user);
        model.addAttribute("sumOrder", totalFormatted);
        return "USER/checkout";
    }
    @PostMapping("/add")
    public String addToCart(Authentication authentication,@AuthenticationPrincipal User user, @RequestParam Long productId, @RequestParam int quantity) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);
        user = users;// Lấy thông tin người dùng từ database
        cartService.addToCart(user, productId, quantity);
        return "redirect:/cart";
    }
    @GetMapping("/remove/{productId}")
    public String removeFromCart(Authentication authentication,@AuthenticationPrincipal User user, @PathVariable Long productId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);
        user = users;// Lấy thông tin người dùng từ database
        cartService.removeFromCart(user, productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(Authentication authentication,@AuthenticationPrincipal User user) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);
        user = users;
        cartService.clearCart(user);
        return "redirect:/cart";
    }
    // Increase quantity
    @PostMapping("/increase/{productId}")
    public String increaseQuantity(@PathVariable Long productId) {
        cartService.increaseQuantity(productId);
        return "redirect:/cart";
    }

    // Decrease quantity
    @PostMapping("/decrease/{productId}")
    public String  decreaseQuantity(@PathVariable Long productId) {
        cartService.decreaseQuantity(productId);
        return "redirect:/cart";
    }
    @PostMapping("/checkout")
    public String checkout(@RequestParam("selectedItems") List<Long> selectedItems, Model model,Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);
        List<CartItem> selectedCartItems = cartService.getCartItems(users).stream()
                .filter(item -> selectedItems.contains(item.getProduct().getId()))
                .collect(Collectors.toList());

        model.addAttribute("selectedCartItems", selectedCartItems);
        model.addAttribute("orderForm", new OrderForm());
        return "USER/orderForm";
    }

    @PostMapping("/placeOrder")
    public String placeOrder(@ModelAttribute OrderForm orderForm, @RequestParam("selectedItems") List<Long> selectedItems,Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);
        List<CartItem> selectedCartItems = cartService.getCartItems(users).stream()
                .filter(item -> selectedItems.contains(item.getProduct().getId()))
                .collect(Collectors.toList());

        List<OrderDetail> orderDetails = selectedCartItems.stream().map(cartItem -> {
            OrderDetail detail = new OrderDetail();
            detail.setProduct(cartItem.getProduct());
            detail.setQuantity(cartItem.getQuantity());
            detail.setPrice(cartItem.getProduct().getPrice());
            return detail;
        }).collect(Collectors.toList());

        Order order = new Order();
        order.setTotalPrice(orderDetails.stream().mapToDouble(detail -> detail.getPrice() * detail.getQuantity()).sum());
        order.setTenNguoiNhan(orderForm.getRecipientName());
        order.setDiachi(orderForm.getRecipientAddress());
        order.setSdt(orderForm.getRecipientPhone());
        order.setEmail(orderForm.getEmail());
        order.setNote(orderForm.getNote());
        order.setPttt(orderForm.getPaymentMethod());

        orderService.createOrder(order, orderDetails);
        return "redirect:/order/confirmation";
    }
}