package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
import DoAnLTUngDung.DoAnLTUngDung.services.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
//@RequestMapping("/")
public class HomeController {

    @Autowired
    private ProductServices productServices;
    @Autowired
    private CategoryServices categoryServices;
    @GetMapping("/")
    public String home(){
        return "html/auth-login-basic";
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
//    @GetMapping("/index")
//    public String showAllProducts(Model model) {
//        List<Category> categories = categoryServices.getAllCategories();
//        model.addAttribute("categories", categories);
//        model.addAttribute("products", productServices.getAllProducts());
//        return "USER/index";
//    }


    @GetMapping("/single/{id}")
    public String showProductDetails(@PathVariable("id") Long id, Model model) {
        model.addAttribute("products", productServices.getAllProducts());
        Product product = productServices.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryServices.getAllCategories());
            return "USER/single";
        }
        return "redirect:/products/list";

    }



//    public String showAllProducts(Model model) {
//        List<Product> products = productServices.getAllProducts();
//        model.addAttribute("products", products);
//        return "Product/DSSanPham";
//    }
@GetMapping("/index")
public String showAllCategories(Model model) {
    List<Category> categories = categoryServices.getAllCategories();
    model.addAttribute("categories", categories);
    model.addAttribute("products", productServices.getAllProducts());
    Map<Category, List<Product>> categoryProductsMap = new HashMap<>();

    for (Category category : categories) {
        List<Product> products = productServices.getProductsByCategoryIdAndHasImage(category.getId());
        // Chỉ thêm vào map nếu có sản phẩm trong danh sách
        if (!products.isEmpty()) {
            categoryProductsMap.put(category, products);
        }
    }


    model.addAttribute("categoryProductsMap", categoryProductsMap);
    return "USER/index";
}


}
