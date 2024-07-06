package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.repository.ICategoryRepository;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        categoryRepository.save(existingCategory);
    }
}
