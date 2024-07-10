package DoAnLTUngDung.DoAnLTUngDung.repository;

import DoAnLTUngDung.DoAnLTUngDung.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IHoaDonRepository extends JpaRepository<HoaDon, Long> {
}