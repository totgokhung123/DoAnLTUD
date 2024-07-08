package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
import DoAnLTUngDung.DoAnLTUngDung.services.ProductServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
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

//    @PostMapping("/add")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
//                             @RequestParam("image") MultipartFile file, Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("categories", categoryServices.getAllCategories());
//            return "Product/Product-add";
//        }
//
//        try {
//            byte[] bytes = file.getBytes();
//            Path path = Paths.get(UPLOADED_DIR + file.getOriginalFilename());
//            Files.write(path, bytes);
//            product.setAnhdaidien("/image/" + file.getOriginalFilename());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        productServices.saveProduct(product);
//        return "redirect:/products/list";
//    }
private final ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
                             @RequestParam("image") MultipartFile file,
                             @RequestParam("images") MultipartFile[] additionalImages,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryServices.getAllCategories());
            return "Product/Product-add";
        }

        try {
            // Xử lý hình ảnh đại diện
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_DIR + file.getOriginalFilename());
            Files.write(path, bytes);
            product.setAnhdaidien("/image/" + file.getOriginalFilename());

            // Xử lý danh sách hình ảnh kèm và chuyển đổi thành chuỗi JSON
            List<String> additionalImagePaths = new ArrayList<>();
            for (MultipartFile additionalImage : additionalImages) {
                bytes = additionalImage.getBytes();
                path = Paths.get(UPLOADED_DIR + additionalImage.getOriginalFilename());
                Files.write(path, bytes);
                additionalImagePaths.add("/image/" + additionalImage.getOriginalFilename());
            }
            String additionalImagePathsJson = objectMapper.writeValueAsString(additionalImagePaths);
            product.setMuTiImagePath(additionalImagePathsJson); // Lưu chuỗi JSON vào MuTiImagePath

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
                    product.setAnhdaidien("/image/" + file.getOriginalFilename());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                product.setAnhdaidien(existingProduct.getAnhdaidien());
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



    @PostMapping("/import-products")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String importProducts(@RequestParam("file") MultipartFile file) {
        try {
            // Kiểm tra file có tồn tại và định dạng hợp lệ
            if (file.isEmpty()) {
                // Xử lý khi file không tồn tại
                return "redirect:/products/list?import_error=empty";
            }
            // Xử lý đọc dữ liệu từ file Excel và thêm sản phẩm vào cơ sở dữ liệu
            List<Product> products = productServices.readProductsFromExcel(file.getInputStream());
            for (Product product : products) {
                // Lưu hoặc thêm product vào cơ sở dữ liệu
                productServices.saveProduct(product);
            }
            return "redirect:/products/list";
        } catch (Exception e) {
            // Xử lý ngoại lệ nếu có lỗi khi đọc hoặc lưu dữ liệu
            return "redirect:/products/list?import_error=" + e.getMessage();
        }
    }


//    //nhapnha
//    @PostMapping("/import-users")
//    public String importUsers(@RequestParam("file") MultipartFile file) {
//        try {
//            // Kiểm tra file có tồn tại và định dạng hợp lệ
//            if (file.isEmpty()) {
//                // Xử lý khi file không tồn tại
//                return "redirect:/userlist?import_error=empty";
//            }
//            // Xử lý đọc dữ liệu từ file Excel và thêm người dùng vào cơ sở dữ liệu
//            List<User> users = userService.readUsersFromExcel(file.getInputStream());
//            for (User user : users) {
//                // Lưu hoặc thêm user vào cơ sở dữ liệu
//                userService.save(user);
//            }
//            return "redirect:/userlist";
//        } catch (Exception e) {
//            // Xử lý ngoại lệ nếu có lỗi khi đọc hoặc lưu dữ liệu
//            return "redirect:/userlist?import_error=" + e.getMessage();
//        }
//    }


    @GetMapping("/export-products")
    public ResponseEntity<InputStreamResource> exportToExcel() throws IOException {
        ByteArrayInputStream in = productServices.exportProductsToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=products.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }

    @PostMapping("/deleteall")
    public String deleteall(@RequestParam("productIds") List<Long> productIds) {
        productServices.deleteall(productIds);
        return "redirect:/products/list";
    }




//    @GetMapping("/delete/{id}")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public String deleteProduct(@PathVariable("id") Long id) {
//        productServices.deleteProduct(id);
//        return "redirect:/products/list";
//    }
}

