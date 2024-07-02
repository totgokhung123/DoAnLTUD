//package DoAnLTUngDung.DoAnLTUngDung.controller;
//
//import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
//import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
//import java.util.List;
//
//@Controller
////@RequestMapping("/categories")
//public class CategoryController {
//
//    @Autowired
//    private CategoryServices categoryServices;
//
//    @GetMapping("/categorylist")
//    //@PreAuthorize("hasAuthority('ADMIN','USER')")
//    public String showAllCategories(Model model) {
//        List<Category> categories = categoryServices.getAllCategories();
//        model.addAttribute("categories", categories);
//        return "ADMIN/DSCategory";
//    }
//
//    @GetMapping("/add")
//    //@PreAuthorize("hasAuthority('ADMIN')")
//    public String showAddForm(Model model) {
//        model.addAttribute("category", new Category());
//        return "ADMIN/Categoryadd";
//    }
//
//    @PostMapping("/add")
//    //@PreAuthorize("hasAuthority('ADMIN')")
//    public String addCategory(@Valid @ModelAttribute("category") Category category, BindingResult result) {
//        if (result.hasErrors()) {
//            System.out.println("Errors: " + result.getAllErrors());
//            return "ADMIN/Categoryadd";
//        }
//        categoryServices.saveCategory(category);
//        return "redirect:/categorylist";
//    }
//
//    @GetMapping("/edit/{id}")
//    //@PreAuthorize("hasAuthority('ADMIN')")
//    public String showEditForm(@PathVariable("id") Long id, Model model) {
//        Category category = categoryServices.getCategoryById(id);
//        if (category != null) {
//            model.addAttribute("category", category);
//            return "ADMIN/Categoryedit";
//        }
//        return "redirect:/categorylist";
//    }
//
////    @PostMapping("/edit/{id}")
////    public String updateCategory(@PathVariable("id") Long id, @Valid @ModelAttribute("category") Category category, BindingResult result) {
////        if (result.hasErrors()) {
////            return "ADMIN/Categoryedit";
////        }
////        categoryServices.saveCategory(category);
////        return "redirect:/categorylist";
////    }
//@PostMapping("/edit/{id}")
//public String updateCategory(@PathVariable("id") Long id,
//                             @Valid @ModelAttribute("category") Category category,
//                             BindingResult result,
//                             @RequestParam("image") MultipartFile imageFile) {
//    if (result.hasErrors()) {
//        return "ADMIN/Categoryedit";
//    }
//
//    if (!imageFile.isEmpty()) {
//        // Xử lý lưu trữ và thay đổi đường dẫn ảnh trong Category tại đây
//        // Ví dụ: lưu file vào thư mục và cập nhật đường dẫn trong category
//        String imagePath = saveImage(imageFile); // Hàm saveImage để lưu file
//        category.setImagePath(imagePath);
//    }
//
//    categoryServices.saveCategory(category);
//    return "redirect:/categorylist";
//}
//
//    private String saveImage(MultipartFile imageFile) {
//        String uploadDir = "assets/img/elements"; // Đường dẫn thư mục lưu trữ ảnh
//        String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename()); // Lấy tên file gốc
//        try {
//            Path uploadPath = Paths.get(uploadDir); // Tạo đường dẫn tuyệt đối cho thư mục lưu trữ
//            if (!Files.exists(uploadPath)) { // Kiểm tra xem thư mục đã tồn tại chưa
//                Files.createDirectories(uploadPath); // Nếu chưa tồn tại, tạo mới
//            }
//            try (InputStream inputStream = imageFile.getInputStream()) {
//                Path filePath = uploadPath.resolve(fileName); // Đường dẫn tuyệt đối của file ảnh
//                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING); // Lưu file vào thư mục
//                return uploadDir + "/" + fileName; // Trả về đường dẫn lưu trữ
//            } catch (IOException e) {
//                throw new RuntimeException("Không thể lưu file " + fileName, e);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Không thể tạo thư mục lưu trữ ảnh", e);
//        }
//    }
//
//    @GetMapping("/delete/{id}")
//    //@PreAuthorize("hasAuthority('ADMIN')")
//    public String deleteCategory(@PathVariable("id") Long id) {
//        categoryServices.deleteCategory(id);
//        return "redirect:/categorylist";
//    }
//}
package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils; // Thêm import này để sử dụng StringUtils

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path; // Thêm import này
import java.nio.file.Paths; // Thêm import này
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
//@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryServices categoryServices;

    @GetMapping("/categorylist")
    //@PreAuthorize("hasAuthority('ADMIN','USER')")
    public String showAllCategories(Model model) {
        List<Category> categories = categoryServices.getAllCategories();
        model.addAttribute("categories", categories);
        return "ADMIN/DSCategory";
    }

    @GetMapping("/categoryadd")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "ADMIN/Categoryadd";
    }

    @PostMapping("/add")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public String addCategory(@Valid @ModelAttribute("category") Category category, BindingResult result) {
        if (result.hasErrors()) {
            System.out.println("Errors: " + result.getAllErrors());
            return "ADMIN/Categoryadd";
        }
        categoryServices.saveCategory(category);
        return "redirect:/categorylist";
    }

    @GetMapping("/edit/{id}")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryServices.getCategoryById(id);
        if (category != null) {
            model.addAttribute("category", category);
            return "ADMIN/Categoryedit";
        }
        return "redirect:/categorylist";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("category") Category category,
                                 BindingResult result,
                                 @RequestParam("image") MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "ADMIN/Categoryedit";
        }

        if (!imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile); // Hàm saveImage để lưu file
            category.setImagePath(imagePath);
        }

        categoryServices.saveCategory(category);
        return "redirect:/categorylist";
    }

    private String saveImage(MultipartFile imageFile) {
        String uploadDir = "assets/img/elements";
        String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                return uploadDir + "/" + fileName;
            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu file " + fileName, e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ ảnh", e);
        }
    }

    @GetMapping("/delete/{id}")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryServices.deleteCategory(id);
        return "redirect:/categorylist";
    }
}