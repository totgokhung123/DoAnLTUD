package DoAnLTUngDung.DoAnLTUngDung.repository;

import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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


    @Query("SELECT u FROM User u WHERE "
            + "(:name IS NULL OR u.name LIKE %:name%) AND "
            + "(:username IS NULL OR u.username LIKE %:username%) AND "
            + "(:email IS NULL OR u.email LIKE %:email%) AND "
            +  "(:sdt IS NULL OR u.sdt LIKE %:sdt%) AND "
            + "(:accountNonLocked IS NULL OR u.accountNonLocked = :accountNonLocked)")
    List<User> findByCriteria(@Param("name") String name,
                              @Param("username") String username,
                              @Param("email") String email,
                              @Param("sdt") String sdt,
                              @Param("accountNonLocked") Boolean accountNonLocked);
//    @Modifying
//    @Transactional
//    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
//    void updateUserEnabledStatus(Long userId, boolean enabled);


}