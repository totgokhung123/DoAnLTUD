package DoAnLTUngDung.DoAnLTUngDung.repository;

import DoAnLTUngDung.DoAnLTUngDung.entity.OrderDetail;
import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface IOrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.product.id = :productId")
    Integer getTotalQuantitySoldByProduct(@Param("productId") Long productId);
}

