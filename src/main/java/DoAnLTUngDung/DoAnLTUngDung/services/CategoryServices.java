package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.repository.ICategoryRepository;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServices {
    @Autowired
    private ICategoryRepository categoryRepository;

    public List<Category> getAllCategories () {
        return categoryRepository.findAll();
    }

//    public Category getCategoryById(Long id) {
//        return categoryRepository.findById(id).orElse(null);
//    }
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void addCategory(Category category) {
        categoryRepository.save(category);
    }
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalStateException("Category with ID " + id + " does not exist.");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void deleteCategories(List<Long> categoryIds) {
        // Xử lý xóa từng danh mục theo id
        for (Long categoryId : categoryIds) {
            categoryRepository.deleteById(categoryId);
        }
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

//    public void deleteCategory(Long id) {
//        categoryRepository.deleteById(id);
//        //updateCategoryIds();
//    }
//    public void updateCategoryIds(@NotNull Category category) {
//        List<Category> categories = categoryRepository.findAll();
//        for (int i = 0; i < categories.size(); i++) {
//            categories.get(i).setId((long) (i + 1));
//        }
//        categoryRepository.saveAll(categories);
//    }
    public void updateCategory(@NotNull Category category) {
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new IllegalStateException("Category with ID " +
                        category.getId() + " does not exist."));
        existingCategory.setImagePath(category.getImagePath());
        existingCategory.setName(category.getName());
        existingCategory.setStatus(category.getStatus());
        categoryRepository.save(existingCategory);
    }

    public List<Category> readCategoryFromExcel(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

        List<Category> categories = new ArrayList<>();

        // Duyệt qua từng dòng của sheet, bắt đầu từ dòng thứ 1 (dòng tiêu đề đã có trong mẫu)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row currentRow = sheet.getRow(i);
            if (currentRow != null) {
                Category category = new Category();
                Cell idCell = currentRow.getCell(0);
                Cell nameCell = currentRow.getCell(1);
                Cell imageCell = currentRow.getCell(2);
                Cell statusCell = currentRow.getCell(3);
                if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                    category.setId((long) idCell.getNumericCellValue());
                }

                if (nameCell != null && nameCell.getCellType() == CellType.STRING) {
                    category.setName(nameCell.getStringCellValue());
                }

                if (imageCell != null && imageCell.getCellType() == CellType.STRING) {
                    category.setImagePath(imageCell.getStringCellValue());
                } else {
                    category.setImagePath(null); // hoặc giá trị mặc định nào đó nếu cần
                }

                if (statusCell != null) {
                    if (statusCell.getCellType() == CellType.BOOLEAN) {
                        category.setStatus(statusCell.getBooleanCellValue());
                    } else if (statusCell.getCellType() == CellType.NUMERIC) {
                        category.setStatus(statusCell.getNumericCellValue() != 0);
                    } else if (statusCell.getCellType() == CellType.STRING) {
                        category.setStatus(Boolean.parseBoolean(statusCell.getStringCellValue()));
                    }
                }
                categories.add(category);
            }
        }

        workbook.close();
        return categories;
    }

    @Transactional
    public void softDeleteCategories(List<Long> categoryIds) {
        for (Long categoryId : categoryIds) {
            softDeleteCategory(categoryId);
        }
    }

    public void softDeleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Category with ID " + id + " does not exist."));
        category.setStatus(false);
        categoryRepository.save(category);

        // Set status of associated products to false
        List<Product> products = category.getProducts();
        if (products != null) {
            for (Product product : products) {
                product.setStatus(false);
            }
        }
    }
}
