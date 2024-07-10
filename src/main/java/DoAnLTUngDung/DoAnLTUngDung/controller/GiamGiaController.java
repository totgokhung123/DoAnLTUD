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
import DoAnLTUngDung.DoAnLTUngDung.repository.IGiamGiaRepository;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/giamgia")
public class GiamGiaController {

    @Autowired
    private IGiamGiaRepository giamGiaRepository;

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
    public String editGiamGia(@PathVariable("id") Long id, Model model) {
        GiamGia giamGia = giamGiaService.getGiamGiaById(id);
        if (giamGia != null) {
            model.addAttribute("giamgia", giamGia);
            return "ADMIN/editCoupon";
        }
        return "redirect:/giamgia/all";
    }

    @PostMapping("/edit/{id}")
    public String editGiamGia(@PathVariable("id") Long id, @Valid @ModelAttribute("giamgia") GiamGia giamGia, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("giamgia", giamGia);
            return "ADMIN/editCoupon"; // Trả về trang chỉnh sửa nếu có lỗi
        }

        GiamGia existingGiamGia = giamGiaService.getGiamGiaById(id);
        if (existingGiamGia == null) {
            model.addAttribute("errorMessage", "Mã giảm giá không được tìm thấy");
            return "redirect:/giamgia/all"; // Trả về trang danh sách mã giảm giá nếu không tìm thấy mã giảm giá
        }

        // Cập nhật thông tin của mã giảm giá hiện có bằng thông tin mới
        existingGiamGia.setMaGiamGia(giamGia.getMaGiamGia());
        existingGiamGia.setDiscountPercentage(giamGia.getDiscountPercentage());
        existingGiamGia.setCondition(giamGia.getCondition());
        existingGiamGia.setStartDate(giamGia.getStartDate());
        existingGiamGia.setEndDate(giamGia.getEndDate());

        // Lưu mã giảm giá đã được cập nhật
        giamGiaRepository.save(existingGiamGia);

        return "redirect:/giamgia/all"; // Trả về trang danh sách mã giảm giá sau khi cập nhật thành công
    }


    @GetMapping("/search")
    public String searchGiamGia() {

        return "ADMIN/editCoupon";
    }

    @GetMapping("/delete/{id}")
    public String deleteGiamGia(@PathVariable Long id) {
        giamGiaService.deleteGiamGia(id);
        return "redirect:/giamgia/all";
    }

}