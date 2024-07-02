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
            return "Product/product-edit";
        }
        return "redirect:/products/list";
    }

    @PostMapping("/edit/{id}")
   // @PreAuthorize("hasAuthority('ADMIN')")
    public String updateProduct(@PathVariable("id") Long id, @Valid @ModelAttribute("product") Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryServices.getAllCategories());
            return "Product/product-edit";
        }
        product.setId(id); // Ensure the ID is set correctly
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
