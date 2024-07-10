package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.ICartItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import DoAnLTUngDung.DoAnLTUngDung.entity.CartItem;
import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.repository.IProductRepository;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
//@SessionScope
@Transactional
public class CartService {
    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ICartItemRepository cartItemRepository;

    public void addToCart(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        CartItem existingCartItem = cartItemRepository.findByUser(user).stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem newCartItem = new CartItem(product, user, quantity);
            cartItemRepository.save(newCartItem);
        }
    }

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    public void removeFromCart(User user, Long productId) {
        cartItemRepository.findByUser(user).stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(cartItemRepository::delete);
    }

    public void clearCart(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(cartItems);
    }

    public double getTotal(User user) {
        return cartItemRepository.findByUser(user).stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    public String getTotalFormatted(User user) {
        double total = getTotal(user);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(total);
    }

    public String formatPrice(double price) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(price);
    }
    public String ehhehehe(double price, Long productId, Long userId) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        CartItem cartItem = cartItemRepository.findByProductIdAndUser_Id(productId, userId);
        return currencyFormatter.format(price * cartItem.getQuantity());
    }

    // Method to increase quantity of CartItem with productId
    public void increaseQuantity(Long productId,User user) {
        CartItem cartItem = findCartItemByUserAndProductId(user,productId);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemRepository.save(cartItem);
        }
    }

    // Method to decrease quantity of CartItem with productId
    public void decreaseQuantity(Long productId,User user) {
        CartItem cartItem = findCartItemByUserAndProductId(user,productId);
        if (cartItem != null && cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartItemRepository.save(cartItem);
        }
    }
   // public String
    private List<CartItem> cartItems = new ArrayList<>();
    // Utility method to find CartItem by productId
    public CartItem findCartItemByProductId(Long productId) {
        return cartItemRepository.findByProductId(productId);
    }
    public CartItem findCartItemByUserAndProductId(User user, Long productId) {
        return cartItemRepository.findByUserAndProductId(user, productId);
    }
    public List<CartItem> findCartItemsByIds(List<Long> productIds, User user) {
        return cartItemRepository.findAllByUser(user).stream()
                .filter(cartItem -> productIds.contains(cartItem.getProduct().getId()))
                .collect(Collectors.toList());
    }
//    public List<CartItem> findCartItemsByProductIds(List<Long> productIds,Long userid) {
//        return cartItemRepository.findByUserAndProductIdInAndSelected(userid, productIds);
//    }

}