package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BaoHanh")
public class BaoHanh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tgbaohanh")
    private String tgbaohanh;

    @Column(name = "dieukienbh")
    private String dieukienbh;
}
