package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.services.ProductServices;
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

    @GetMapping("/")
    public String home(){
        return "html/auth-login-basic";
    }
    @GetMapping("/auth/forgot_password")
    public String showfogot(){
        return "ADMIN/forgotpassword";
    }
    @GetMapping("/home")
    public String test(){
        return "html/index";
    }

    /// test chức nănag template
    @GetMapping("/testedit")
    public String edit(){
        return "ADMIN/editUser";
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
