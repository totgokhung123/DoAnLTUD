package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "GiamGia")
@Data
@NoArgsConstructor
public class GiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten", nullable = false)
    private String maGiamGia; // Tên chương trình giảm giá

    @Column(name = "phantram", nullable = false)
    private int discountPercentage; // Phần trăm giảm giá

    @Column(name = "dieukien")
    private String condition; // Điều kiện áp dụng giảm giá

    @Column(name = "ngaytao")
    private LocalDate startDate; // Ngày bắt đầu

    @Column(name = "ngayketthuc")
    private LocalDate endDate; // Ngày kết thúc

    public double getPhanTramGiam() {
        return this.discountPercentage;
    }
}