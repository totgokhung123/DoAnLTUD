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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String showCart(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userServices.findByUsername(username);

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
    public String addToCart(Authentication authentication, @RequestParam Long productId, @RequestParam int quantity) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);
        cartService.addToCart(users, productId, quantity);
        return "redirect:/cart";
    }
    @GetMapping("/remove/{productId}")
    public String removeFromCart(Authentication authentication, @PathVariable Long productId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);

        cartService.removeFromCart(users, productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);
        cartService.clearCart(users);
        return "redirect:/cart";
    }
    // Increase quantity
    @PostMapping("/increase/{productId}")
    public String increaseQuantity(@PathVariable Long productId,Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);
        cartService.increaseQuantity(productId,users);
        return "redirect:/cart";
    }

    // Decrease quantity
    @PostMapping("/decrease/{productId}")
    public String  decreaseQuantity(@PathVariable Long productId,Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User users = userServices.findByUsername(username);
        cartService.decreaseQuantity(productId,users);
        return "redirect:/cart";
    }
    @PostMapping("/checkout")
    public String checkout(@RequestParam("selectedProducts") List<Long> selectedProductIds, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("selectedProducts", selectedProductIds);
        return "redirect:/order"; // Chuyển hướng đến GET request để hiển thị form đơn hàng
    }
}