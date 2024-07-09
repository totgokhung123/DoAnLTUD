package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.GiamGia;
import DoAnLTUngDung.DoAnLTUngDung.services.GiamGiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/giamgia")
public class GiamGiaController {

    @Autowired
    private GiamGiaService giamGiaService;

    // Lấy tất cả mã giảm giá
    @GetMapping("/all")
    public String getAllGiamGia(Model model) {

        List<GiamGia> giamGiaList = giamGiaService.getAllGiamGia();
        model.addAttribute("DSGiamGia", giamGiaList);
        return "ADMIN/DSCoupon";
    }

    @GetMapping("/add")
    public String addGiamGia(Model model) {
        model.addAttribute("giamgia", new GiamGia());
        return "ADMIN/addCoupon";
    }

    @PostMapping("/add")
    public String addGiamGia(@Valid GiamGia giamGia, BindingResult result) {
        if (result.hasErrors()) {
            return "ADMIN/LayoutAdmin";
        }
        giamGiaService.addGiamGia(giamGia);
        return "redirect:/giamgia/all";
    }

    @GetMapping("/edit/{id}")
    public String editGiamGia(@PathVariable Long id, Model model) {
        GiamGia giamGia = giamGiaService.getGiamGiaById(id);
        model.addAttribute("giamGia", giamGia);
        return "ADMIN/editCoupon";
    }

    @PostMapping("/edit")
    public String editGiamGia(@ModelAttribute GiamGia giamGia) {
        giamGiaService.updateGiamGia(giamGia);
        return "redirect:/giamgia/all";
    }

    @GetMapping("/delete/{id}")
    public String deleteGiamGia(@PathVariable Long id) {
        giamGiaService.deleteGiamGia(id);
        return "redirect:/giamgia/all";
    }




}