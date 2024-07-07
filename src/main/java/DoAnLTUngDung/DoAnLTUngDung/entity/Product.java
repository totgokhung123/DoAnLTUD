package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 100, message = "Tên sản phẩm phải ít hơn 100 ký tự")
    @Column(name = "title")
    private String title;

    @Min(value = 10000, message = "Giá sản phẩm phải ít nhất lớn hơn 10,000")
    @Column(name = "price")
    private double price;

    @Min(value = 1, message = "Số lượng sản phẩm phải ít nhất lớn hơn 1")
    @Column(name = "sl")
    private double sl;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    @Size(max = 500, message = "Mô tả sản phẩm phải ít hơn 500 ký tự")
    @Column(name = "description")
    private String description;

    @Past(message = "Năm sản xuất phải là quá khứ")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "NamSX")
    private Date NamSX;

    // ảnh sẽ hiện trên index
    @Column(name = "anhdaidien")
    private String anhdaidien;

    // sau khi click checkout product gọi thằng này
    @Column(name = "MuTiImagePath")
    private String MuTiImagePath;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_path")
    private List<String> imagePaths;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    // phan thêm mới database
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DanhGia> danhGias;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<productGiamGia> giamGias;

    @ManyToOne
    @JoinColumn(name = "baoHanh_id", referencedColumnName = "id")
    private BaoHanh baoHanh;
}
