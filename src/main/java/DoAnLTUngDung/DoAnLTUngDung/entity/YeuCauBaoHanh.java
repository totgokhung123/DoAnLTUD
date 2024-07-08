package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;

import java.util.Date;
// quản lý yeu cau
@Entity
@Table(name = "YeuCauBaoHanh")
public class YeuCauBaoHanh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Temporal(TemporalType.DATE)
    @Column(name = "ngayyeucau")
    private Date ngayyeucau;

    @Column(name = "status")
    private String status; // Trạng thái yêu cầu (ví dụ: "pending", "approved", "rejected")

    @Column(name = "mota")
    private String mota;
}
