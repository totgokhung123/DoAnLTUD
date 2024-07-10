package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.DanhGia;
import DoAnLTUngDung.DoAnLTUngDung.entity.HoaDon;
import DoAnLTUngDung.DoAnLTUngDung.repository.IHoaDonRepository;
import DoAnLTUngDung.DoAnLTUngDung.services.DanhGiaService;
import DoAnLTUngDung.DoAnLTUngDung.services.HoadonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HoaDonController {
    @GetMapping("/inhoadon")
    public String checkout() {
        return "HOADON/invoice-4";
    }

    @Autowired
    private HoadonService hoadonService;
    @Autowired
    private IHoaDonRepository hoaDonRepository;


    @GetMapping("/hoadonlist")
    public String showAllHoaDon(Model model) {
        List<HoaDon> hoaDonList = hoadonService.getAllHoaDon();
        model.addAttribute("hoadons", hoaDonList);
        return "ADMIN/DSHoaDon";
    }
//    @DeleteMapping("/deleteHoaDon/{id}")
//    public String deleteHoaDon(@PathVariable("id") Long id) {
//        hoadonService.deleteHoaDonById(id);
//        return "redirect:/hoadonlist";
//    }
    @GetMapping("/deleteHoaDon/{id}")
    public String deleteHoaDon(@PathVariable("id") Long id) {
        hoaDonRepository.deleteById(id);
        return "redirect:/hoadonlist";
    }
}
