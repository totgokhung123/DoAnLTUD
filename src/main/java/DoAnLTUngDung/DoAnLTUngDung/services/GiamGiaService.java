package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.GiamGia;
import DoAnLTUngDung.DoAnLTUngDung.repository.IGiamGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GiamGiaService {
    @Autowired
    private IGiamGiaRepository giamGiaRepository;

    public GiamGia addGiamGia(GiamGia giamGia) {
        return giamGiaRepository.save(giamGia);
    }

    public GiamGia updateGiamGia(GiamGia giamGia) {
        return giamGiaRepository.save(giamGia);
    }

    public void deleteGiamGia(Long id) {
        giamGiaRepository.deleteById(id);
    }

    public GiamGia getGiamGiaById(Long id) {
        return giamGiaRepository.findById(id).orElse(null);
    }

    public GiamGia getGiamGiaByMaGiamGia(String maGiamGia) {
        return giamGiaRepository.findByMaGiamGia(maGiamGia).orElse(null);
    }

    public List<GiamGia> getAllGiamGia() {
        return giamGiaRepository.findAll();
    }
}