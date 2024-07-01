package DoAnLTUngDung.DoAnLTUngDung.entity;

import DoAnLTUngDung.DoAnLTUngDung.Validator.ValidCategoryId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
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

    @NotBlank(message = "mô tả sản phẩm không được để trống")
    @Size(max = 500, message = "mô tả phải ít hơn 500 ký tự")
    @Column(name = "description")
    private String description;

    @Future(message = "năm sản xuất không trước tương lai")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy")
    @Column(name = "NamSX")
    private Date NamSX;

    @Column(name = "MuTiImagePath")
    private String MuTiImagePath;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_path")
    private List<String> imagePaths;

    @ManyToOne(fetch = FetchType.LAZY)
    @ValidCategoryId
    private Category category;
}
