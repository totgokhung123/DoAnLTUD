package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
import DoAnLTUngDung.DoAnLTUngDung.services.ProductServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @Autowired
    private ProductServices productService;
    @GetMapping("/Admin")
    public String adminHome() {
        return "ADMIN/LayoutAdmin";
    }
    @GetMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ADMIN")) {
            return "redirect:/Admin";
        }
        return "redirect:/";
    }
    @GetMapping("/single/{id}")
    public String showProductDetails(@PathVariable("id") Long id, Model model,HttpServletRequest request) {
        if (!request.isUserInRole("USER") && !request.isUserInRole("ADMIN"))
        {
            return "redirect:/auth-login-basic";
        }
        model.addAttribute("products", productServices.getAllProducts());
        Product product = productServices.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryServices.getAllCategories());
            return "USER/single";
        }
        return "redirect:/products/list";

    }
    @GetMapping("/")
    public String showAllCategories(Model model) {
        List<Category> categories = categoryServices.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("products", productServices.getAllProducts());
//        String[] searchParams = query.split(";");
//        String title = searchParams.length > 0 ? searchParams[0] : "";
//        String categoryName = searchParams.length > 1 ? searchParams[1] : "";
//        List<Product> productss= productService.searchProducts(title, categoryName);
//        model.addAttribute("products", productss);
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

    @GetMapping("/search-products")
    public String searchProducts(@RequestParam(name="query", required = false) String query , Model model) {
        List<Product> products = productService.searchProducts(query);
        model.addAttribute("products", products);
        return "USER/product";
    }
}
