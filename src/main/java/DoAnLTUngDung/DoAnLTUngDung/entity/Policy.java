package DoAnLTUngDung.DoAnLTUngDung.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ChinhSach")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // "shipping" or "warranty"

    @Column(name = "description",length = 200, nullable = false)
    @Size(max = 16000, message = "Comment không quá 16000 ký tự!")
    @NotBlank(message = "Comment không được để trống!")
    private String description;

}

