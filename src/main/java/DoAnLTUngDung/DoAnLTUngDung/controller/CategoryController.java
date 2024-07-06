package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.services.CategoryServices;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
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

    @PostMapping("/delete/selected")
    public ResponseEntity<String> deleteSelectedCategories(@RequestBody List<Long> categoryIds) {
        try {
            // Gọi service để xóa danh mục
            categoryServices.deleteCategories(categoryIds);
            return ResponseEntity.ok("Đã xóa thành công danh mục đã chọn.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi xóa danh mục: " + e.getMessage());
        }
    }

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

//    @GetMapping("/delete/{id}")
//    public String deleteCategory(@PathVariable("id") Long id) {
//        categoryServices.deleteCategory(id);
//
//        return "redirect:/categorylist";
//    }
//    @GetMapping("/delete/{id}")
//    public String deleteCategory(@PathVariable("id") Long id, Model model) {
//        Category category = categoryServices.getCategoryById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:"
//                        + id));
//        categoryServices.deleteCategoryById(id);
//        model.addAttribute("category", category);
//        return "ADMIN/Categorydelete";
//    }
        @GetMapping("/delete/{id}")
        //@PreAuthorize("hasAuthority('ADMIN')")
        public String deleteCategory(@PathVariable("id") Long id) {
            categoryServices.deleteCategory(id);
            return "redirect:/ADMIN/Categorylist";
        }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportCategoriesToExcel() {
        try {
            List<Category> categories = categoryServices.getAllCategories();

            // Tạo workbook mới
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Category List");

            // Tạo header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Category ID");
            header.createCell(1).setCellValue("Category Name");
            header.createCell(2).setCellValue("Image Path");

            // Thêm dữ liệu từ danh sách categories vào sheet
            int rowNum = 1;
            for (Category category : categories) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
                row.createCell(2).setCellValue(category.getImagePath());
            }

            // Ghi workbook ra ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            // Chuẩn bị các headers cho file Excel
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.attachment().filename("categories.xlsx").build());

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
