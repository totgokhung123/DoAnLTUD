package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
@Table(name = "DanhGia")
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "rating" ,nullable = false)
    private int rating;

    @Column(name = "comment",length = 200, nullable = false)
    @Size(max = 200, message = "comment không quá 200 ký tự!")
    @NotBlank(message = "Comment không được để trống!")
    private String comment;

    @Temporal(TemporalType.DATE)
    @Column(name = "ngaydanhgia")
    private Date ngaydanhgia;



}
