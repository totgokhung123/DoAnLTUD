package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.DanhGia;
import DoAnLTUngDung.DoAnLTUngDung.services.DanhGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DanhGiaController {

    @Autowired
    private DanhGiaService danhGiaService;

    @GetMapping("/danhgialist")
    public String showAllDanhGia(Model model) {
        List<DanhGia> danhGiaList = danhGiaService.getAllDanhGia();
        model.addAttribute("danhgias", danhGiaList);
        return "ADMIN/DSDanhGia";
    }
    @PostMapping("/deleteDanhGia")
    public String deleteDanhGia(@RequestParam("id") Long id) {
        danhGiaService.deleteDanhGiaById(id);
        return "redirect:/danhgialist";
    }
}