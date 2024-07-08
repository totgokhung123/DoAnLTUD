package DoAnLTUngDung.DoAnLTUngDung.repository;

import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByPriceDesc();
    List<Product> findByCategoryId(Long categoryId);
    @Query("SELECT p FROM Product p WHERE p.specialOffer = true")
    List<Product> findSpecialOffers();
}

