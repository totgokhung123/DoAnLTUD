package DoAnLTUngDung.DoAnLTUngDung.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HoaDonController {
    @GetMapping("/inhoadon")
    public String checkout() {
        return "HOADON/invoice-4";
    }
}
