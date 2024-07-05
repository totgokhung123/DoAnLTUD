package DoAnLTUngDung.DoAnLTUngDung.repository;

import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.id = ?1")
    User findByUserId(Long userId);
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.resetToken = ?1")
    Optional<User> findByResetToken(String resetToken);

    @Modifying
    @Transactional
    @Query (value = "INSERT INTO user_role(user_id,role_id) VALUE(?1,?2) ", nativeQuery = true)
    void addRoleToUser(Long userId,Long roleId);


    @Query(value = "SELECT COUNT(*) FROM user_role WHERE user_id = :userId AND role_id = :roleId", nativeQuery = true)
    int countUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query("SELECT u.id FROM User u WHERE u.username  = ?1")
    Long getUserIdByUsername(String username);

    @Query(value = "SELECT r.name FROM role r INNER JOIN user_role ur ON r.id = ur.role_id WHERE ur.user_id =?1", nativeQuery = true)
    String[] getRoleOfUser(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.active = active WHERE u.id = userId")
    void updateActiveStatus(@Param("userId") Long userId, @Param("active") boolean active);


}