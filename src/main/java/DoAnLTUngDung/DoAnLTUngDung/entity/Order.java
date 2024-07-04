package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên khách Hàng nhận không được để trống")
    @Size(min = 10,max = 32, message = "Tên người nhận  ít nhất 10 ký tự và lớn nhất 32 ký tự")
    @Column(name = "TenNguoiNhan")
    private String TenNguoiNhan;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(min = 10,max = 15, message = "Số điện thoại ít nhất 10 số lớn nhất 14 số")
    @Column(name = "sdt")
    private String sdt;

    @NotBlank(message = "Địa Chỉ không được để trống")
    @Size(min = 10, message = "Địa chỉ ít nhất 10 ký tự")
    @Column(name = "diachi")
    private String diachi;

    @Email(message = "Email phải hợp lệ")
    @NotNull(message = "Email không được để trống")
    @Column(name = "Email")
    private String email;

    private String note;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Column(name = "pttt")
    private String pttt;


    @OneToMany(mappedBy = "order" ,cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;


    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HoaDon hoaDon;
}
