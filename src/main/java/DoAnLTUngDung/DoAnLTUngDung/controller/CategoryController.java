package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
//@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryServices categoryServices;
    private static final String UPLOADED_DIR = "src/main/resources/static/assets/img/category/";
    @GetMapping("/categorylist")
    public String showAllCategories(Model model) {
        List<Category> categories = categoryServices.getAllCategories();
        model.addAttribute("categories", categories);
        return "ADMIN/DSCategory";
    }


        @GetMapping("/add")
        public String showAddForm(Model model) {
            model.addAttribute("category", new Category());
            return "ADMIN/Categoryadd";
        }
        @PostMapping("/add")
        public String addCategory(@Valid Category category, @RequestParam("image") MultipartFile file, BindingResult result) {
            if (result.hasErrors()) {
                return "ADMIN/Categoryadd";
            }
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOADED_DIR+file.getOriginalFilename());
                Files.write(path,bytes);
                category.setImagePath("assets/img/category/" + file.getOriginalFilename());
            }catch (IOException e){
                e.printStackTrace();
            }
            categoryServices.addCategory(category);
            return "redirect:/categorylist";
        }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryServices.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:"
                        + id));
        model.addAttribute("category", category);
        return "ADMIN/Categoryedit";
    }
    // POST request to update category
    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable("id") Long id, @Valid Category category,
                                 BindingResult result, @RequestParam("image") MultipartFile file, Model model) {
        if (result.hasErrors()) {
            category.setId(id);
            return "ADMIN/Categoryedit";
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_DIR+file.getOriginalFilename());
            Files.write(path,bytes);
            category.setImagePath("assets/img/category/" + file.getOriginalFilename());
        }catch (IOException e){
            e.printStackTrace();
        }
        categoryServices.updateCategory(category);
        model.addAttribute("categories", categoryServices.getAllCategories());
        return "redirect:/categorylist";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryServices.deleteCategory(id);
        return "redirect:/categorylist";
    }
}
