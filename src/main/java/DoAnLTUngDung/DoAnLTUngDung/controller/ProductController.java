package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
import DoAnLTUngDung.DoAnLTUngDung.services.ProductServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductServices productServices;

    @Autowired
    private CategoryServices categoryServices;

    @GetMapping("/list")
    public String showAllProducts(Model model) {
        List<Product> products = productServices.getAllProducts();
        model.addAttribute("products", products);
        return "Product/DSSanPham";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryServices.getAllCategories());
        return "Product/Product-add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryServices.getAllCategories());
            return "Product/Product-add";
        }
        productServices.saveProduct(product);
        return "redirect:/products/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productServices.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryServices.getAllCategories());
            return "Product/editpro";
        }
        return "redirect:/products/list";
     //   return "Product/editpro";
    }


//    @GetMapping("/edit/{id}")
//    public String editUser(@PathVariable("id") Long id, Model model) {
//        User user = userService.getUsersById(id);
//        if (user != null) {
//            model.addAttribute("user", user);
//            return "ADMIN/editUser"; // Thay đổi đường dẫn và tên file thích hợp
//        }
//        // Xử lý trường hợp không tìm thấy user
//        return "redirect:/userlist";
//    }






    @PostMapping("/edit")
   // @PreAuthorize("hasAuthority('ADMIN')")
    public String updateProduct(@Valid @ModelAttribute("product") Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryServices.getAllCategories());
            return "Product/editpro";
        }
       // product.setId(id); // Ensure the ID is set correctly
        productServices.saveProduct(product);
        return "redirect:/products/list";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteProduct(@PathVariable("id") Long id) {
        productServices.deleteProduct(id);
        return "redirect:/products/list";
    }
}
