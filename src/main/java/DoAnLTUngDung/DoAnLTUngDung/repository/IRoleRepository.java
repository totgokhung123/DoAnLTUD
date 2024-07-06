package DoAnLTUngDung.DoAnLTUngDung.repository;

import DoAnLTUngDung.DoAnLTUngDung.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IRoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r.id From Role r Where r.name = ?1")
    Long getRoleIdByName(String roleName);
    @Query("SELECT r FROM Role r WHERE r.name = ?1")
    Role findByName(String roleName);
    @Query("SELECT r FROM Role r")
    List<Role> findAllRoles();
}

