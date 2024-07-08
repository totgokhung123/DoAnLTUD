package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "GiamGia")
public class GiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phantram", nullable = false)
    private int phantram; // Phần trăm giảm giá

    @Column(name = "dieukien")
    private String dieukien; // Điều kiện áp dụng giảm giá

    @Temporal(TemporalType.DATE)
    @Column(name = "ngaytao")
    private Date ngaytao; // Ngày bắt đầu

    @Temporal(TemporalType.DATE)
    @Column(name = "ngayketthuc")
    private Date ngayketthuc; // Ngày kết thúc
}
