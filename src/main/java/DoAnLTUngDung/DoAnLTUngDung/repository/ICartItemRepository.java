package DoAnLTUngDung.DoAnLTUngDung.repository;

import DoAnLTUngDung.DoAnLTUngDung.entity.CartItem;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUser(User user);
    List<CartItem> findByUser(User user);
    CartItem findByProductId(Long productId);
    CartItem findByUserAndProductId(User user, Long productId);
    CartItem findByProductIdAndUser_Id(Long productId, Long userId);
    //List<CartItem> findByUserAndProductIdInAndSelected(Long userId, List<Long> productIds);
}