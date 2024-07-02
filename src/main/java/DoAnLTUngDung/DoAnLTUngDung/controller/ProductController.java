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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
@Controller
@RequestMapping("/products")
public class ProductController {
    private static final String UPLOADED_DIR = "src/main/resources/static/image/";

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
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
                             @RequestParam("image") MultipartFile file, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryServices.getAllCategories());
            return "Product/Product-add";
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_DIR + file.getOriginalFilename());
            Files.write(path, bytes);
            product.setMuTiImagePath("/image/" + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }

        productServices.saveProduct(product);
        return "redirect:/products/list";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productServices.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryServices.getAllCategories());
            return "Product/editpro";
        }
        return "redirect:/products/list";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String updateProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
                                @RequestParam(value = "image", required = false) MultipartFile file, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryServices.getAllCategories());
            return "Product/editpro";
        }

        // Load existing product to preserve original image path if no new image uploaded
        Product existingProduct = productServices.getProductById(product.getId());
        if (existingProduct != null) {
            if (file != null && !file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(UPLOADED_DIR + file.getOriginalFilename());
                    Files.write(path, bytes, StandardOpenOption.CREATE);
                    product.setMuTiImagePath("/image/" + file.getOriginalFilename());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                product.setMuTiImagePath(existingProduct.getMuTiImagePath());
            }
        }

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

