package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
import DoAnLTUngDung.DoAnLTUngDung.services.ProductServices;
import DoAnLTUngDung.DoAnLTUngDung.services.UserService;
import DoAnLTUngDung.DoAnLTUngDung.services.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Autowired
    private ProductServices productService;
    @Autowired
    private UserServices userServices;
    @GetMapping("/Admin")
    public String adminHome(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userServices.findByUsername(username); // Lấy thông tin người dùng từ database
        model.addAttribute("user", user);
        return "ADMIN/LayoutAdmin";
    }
    @GetMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ADMIN")) {
            return "redirect:/Admin";
        }
        return "redirect:/";
    }
    @GetMapping("/about")
    public String aobut() {
        return "USER/about";
    }
    @GetMapping("/lienhe")
    public String lienhe() {
        return "USER/lienhe";
    }
//    @GetMapping("/single/{id}")
//    public String showProductDetails(@PathVariable("id") Long id, Model model) {
//        List<Category> categories = categoryServices.getAllCategories();
//        model.addAttribute("categories", categories);
//        model.addAttribute("products", productServices.getAllProducts());
//
//        // Lấy thông tin sản phẩm theo ID
//        Product product = productServices.getProductById(id);
//        if (product != null) {
//            model.addAttribute("product", product);
//            return "USER/single";
//        }
//        // Nếu sản phẩm không tồn tại, chuyển hướng đến danh sách sản phẩm
//        return "redirect:/products/list";
//    }

    @GetMapping("/single/{id}")
    public String showProductDetails(@PathVariable("id") Long id, Model model) {
        List<Category> categories = categoryServices.getAllCategories()
                .stream()
                .filter(Category::getStatus)
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);

        List<Product> products = productServices.getAllProducts()
                .stream()
                .filter(Product::getStatus)
                .collect(Collectors.toList());
        model.addAttribute("products", products);

        // Lấy thông tin sản phẩm theo ID
        Product product = productServices.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            return "USER/single";
        }
        // Nếu sản phẩm không tồn tại, chuyển hướng đến danh sách sản phẩm
        return "redirect:/products/list";
    }
    @GetMapping("/")
    public String showAllCategories(Model model) {
        List<Category> categories = categoryServices.getAllCategories()
                .stream()
                .filter(Category::getStatus)
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);

        List<Product> products = productServices.getAllProducts()
                .stream()
                .filter(Product::getStatus)
                .collect(Collectors.toList());
        model.addAttribute("products", products);
        List<Product> productssu = productServices.getSpecialOffers();
        model.addAttribute("productsu", productssu);
        Map<Category, List<Product>> categoryProductsMap = categories.stream()
                .collect(Collectors.toMap(
                        category -> category,
                        category -> productServices.getProductsByCategoryIdAndHasImage(category.getId())
                                .stream()
                                .filter(Product::getStatus)
                                .collect(Collectors.toList())
                ));

        // Loại bỏ các mục có danh sách sản phẩm rỗng
        categoryProductsMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        model.addAttribute("categoryProductsMap", categoryProductsMap);
        return "USER/index";
    }
    @GetMapping("/category/{id}")
    public String theloai(@PathVariable("id") Long id, Model model) {
        List<Product> products = productServices.getProductsByCategory(id)
                .stream()
                .filter(Product::getStatus)
                .collect(Collectors.toList());
        if (products.isEmpty()) {
            model.addAttribute("message", "Chưa có sản phẩm nào trong thể loại này.");
        } else {
            Map<Category, List<Product>> categoryProductsMap = products.stream()
                    .collect(Collectors.groupingBy(Product::getCategory));
            model.addAttribute("categoryProductsMap", categoryProductsMap);
        }
        return "USER/theloai";
    }
    @GetMapping("/search-products")
    public String searchProducts(@RequestParam(name = "query", required = false) String query, Model model) {
        List<Product> products = productServices.searchProducts(query)
                .stream()
                .filter(Product::getStatus)
                .collect(Collectors.toList());
        model.addAttribute("products", products);
        return "USER/product";
    }
}
