package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.services.ProductServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
//@RequestMapping("/")
public class HomeController {

    @Autowired
    private ProductServices productServices;
    @GetMapping("/Admin")
    public String adminHome() {
        return "ADMIN/LayoutAdmin";
    }
    @GetMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ADMIN")) {
            return "redirect:/Admin";
        }
        return "redirect:/index";
    }
    @GetMapping("/index")
    public String showAllProducts(Model model) {
        model.addAttribute("products", productServices.getAllProducts());
        return "USER/index";
    }

    @GetMapping("/single/{id}")
    public String showProductDetails(@PathVariable("id") Long id, Model model) {
        Product product = productServices.getProductById(id);
        if (product != null) {
            productServices.updateProductDetails(product); // Cập nhật chi tiết sản phẩm trước khi hiển thị
            model.addAttribute("product", product);
            return "/USER/single";
        } else {
            return "redirect:/products/list"; // Chuyển hướng nếu không tìm thấy sản phẩm
        }
    }


    @GetMapping("/checkout")
    public String checkout(){
        return "USER/checkout";
    }

}
