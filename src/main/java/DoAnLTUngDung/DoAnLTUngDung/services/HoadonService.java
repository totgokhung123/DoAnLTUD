package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.DanhGia;
import DoAnLTUngDung.DoAnLTUngDung.entity.HoaDon;
import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.repository.IDanhGiaRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IHoaDonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class HoadonService {

    @Autowired
    private IHoaDonRepository hoaDonRepository;

    public List<HoaDon> getAllHoaDon() {
        return hoaDonRepository.findAll();
    }

    public void deleteHoaDonById(Long id) {
        hoaDonRepository.deleteById(id);
    }
    @Transactional
    public void deleteHoaDon(List<Long> hoadonIds) {
        for (Long hoadonId : hoadonIds) {
            hoaDonRepository.deleteById(hoadonId);
        }
    }
}
