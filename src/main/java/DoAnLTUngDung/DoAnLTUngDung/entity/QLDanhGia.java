package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "QLDanhGia")
public class QLDanhGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "danhGia_id", referencedColumnName = "id")
    private DanhGia danhGia;


    @Column(name = "status", nullable = false)
    private String status; // Ví dụ: "pending", "approved", "rejected"

//    @ManyToOne
//    @JoinColumn(name = "admin_id", referencedColumnName = "id")
//    private User admin;
    @Temporal(TemporalType.DATE)
    @Column(name = "NgayHD")
    private Date NgayHD;
}
