package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;

// liên kết các sản phẩm với các chương trình giảm giá
@Entity
@Table(name = "productGiamGia")
public class productGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "giamGia_id", referencedColumnName = "id")
    private GiamGia giamGia;
}
