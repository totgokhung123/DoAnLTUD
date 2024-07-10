package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.DanhGia;
import DoAnLTUngDung.DoAnLTUngDung.repository.IDanhGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DanhGiaService {

    @Autowired
    private IDanhGiaRepository danhGiaRepository;

    public List<DanhGia> getAllDanhGia() {
        return danhGiaRepository.findAll();
    }

    public void deleteDanhGiaById(Long id) {
        danhGiaRepository.deleteById(id);
    }
}