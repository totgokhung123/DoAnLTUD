package DoAnLTUngDung.DoAnLTUngDung.repository;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String categoryName);

}
