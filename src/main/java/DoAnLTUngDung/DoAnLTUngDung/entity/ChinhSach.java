package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
@Table(name = "ChinhSach")
public class ChinhSach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenchinhsach",length = 50, nullable = false)
    @Size(max = 50, message = "Tên chính sách không quá 50 ky tự")
    @NotBlank(message = "Tên chính sách không được để trống")
    private String tenchinhsach;

    @Column(name = "mota", nullable = false)
    @NotBlank(message = "Mô tả chính sách không được để trống")
    private String mota;

    @Temporal(TemporalType.DATE)
    @Column(name = "ngaytao")
    private Date ngaytao;

    @Temporal(TemporalType.DATE)
    @Column(name = "ngaycapnhat")
    private Date ngaycapnhat;
}
