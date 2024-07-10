package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@Data
@Entity
@Table(name = "HoaDon")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  //  @Future(message = "Ngày lập hóa đơn không trước tương lai")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "NgayLap")
    private Date NgayLap;

    @NotBlank(message = "Trạng thái không được để trống")
    @Size(max = 20)
    @Column(name = "TrangThai")
    private String TrangThai;

    @Column(name = "totalPrice")
    private double totalPrice;
    @OneToOne
    @JoinColumn(name = "order_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_HoaDon_Order"))
    private Order order;


}
