package DoAnLTUngDung.DoAnLTUngDung.repository;

import DoAnLTUngDung.DoAnLTUngDung.entity.GiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface IGiamGiaRepository extends JpaRepository<GiamGia, Long> {
    Optional<GiamGia> findByMaGiamGia(String maGiamGia);
}