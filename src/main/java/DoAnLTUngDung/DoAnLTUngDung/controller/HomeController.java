package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
import DoAnLTUngDung.DoAnLTUngDung.services.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Category> categories = categoryServices.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("products", productServices.getAllProducts());

        // Lấy thông tin sản phẩm theo ID
        Product product = productServices.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            return "USER/single";
        }
        // Nếu sản phẩm không tồn tại, chuyển hướng đến danh sách sản phẩm
        return "redirect:/products/list";
    }



    @GetMapping("/index")
    public String showAllCategories(Model model) {
    List<Category> categories = categoryServices.getAllCategories();
    model.addAttribute("categories", categories);
    model.addAttribute("products", productServices.getAllProducts());
    Map<Category, List<Product>> categoryProductsMap = new HashMap<>();
    for (Category category : categories) {
        List<Product> products = productServices.getProductsByCategoryIdAndHasImage(category.getId());
        if (!products.isEmpty()) {
            categoryProductsMap.put(category, products);
        }
    }
    model.addAttribute("categoryProductsMap", categoryProductsMap);
    return "USER/index";
}

//    @GetMapping("/category/{id}")
//    public String theloai(@PathVariable("id") Long id, Model model) {
//        List<Product> products = productServices.getProductsByCategoryId(id);
//        if (!products.isEmpty()) {
//            model.addAttribute("products", products);
//            model.addAttribute("categories", categoryServices.getAllCategories());
//            return "USER/theloai";
//        }
//        return "redirect:/USER/theloai";
//    }
@GetMapping("/category/{id}")
public String theloai(@PathVariable("id") Long id, Model model) {
    List<Product> products = productServices.getProductsByCategory(id);
    if (products.isEmpty()) {
        model.addAttribute("message", "Chưa có sản phẩm nào trong thể loại này.");
    } else {
        Map<Category, List<Product>> categoryProductsMap = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory));
        model.addAttribute("categoryProductsMap", categoryProductsMap);
    }
    return "USER/theloai";
}







}
