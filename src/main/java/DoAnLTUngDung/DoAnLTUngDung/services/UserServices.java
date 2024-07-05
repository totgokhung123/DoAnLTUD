package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.Role;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.IRoleRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IUserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class UserServices {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository rolePepository;
    public void save(User user) {
        userRepository.save(user);
        Long userId = userRepository.getUserIdByUsername(user.getUsername());

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Long roleId = rolePepository.getRoleIdByName("USER");
            if (roleId != 0 && userId != 0) {
                userRepository.addRoleToUser(userId, roleId);
            }
        } else {
            // Gán vai trò từ user nếu đã có
            for (Role role : user.getRoles()) {
                Long roleId = rolePepository.getRoleIdByName(role.getName());
                if (roleId != 0 && userId != 0 && userRepository.countUserRole(userId, roleId) == 0) {
                    userRepository.addRoleToUser(userId, roleId);
                }
            }
        }
    }
    public List<User> getAllusers () {
        return userRepository.findAll();
    }
    public User getUsersById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUsers(User user) {
        return userRepository.save(user);
    }

    public void deleteUsers(Long id) {
        userRepository.deleteById(id);
    }

    public User edit(User editedUser) {
        User existingUser = userRepository.findById(editedUser.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setUsername(editedUser.getUsername());
            existingUser.setPassword(new BCryptPasswordEncoder().encode(editedUser.getPassword()));
            existingUser.setEmail(editedUser.getEmail());
            existingUser.setName(editedUser.getName());
            return userRepository.save(existingUser);
        }
        return null;
    }
    public void deleteMultipleUsers(List<Long> userIds) {
        for (Long id : userIds) {
            userRepository.deleteById(id);
        }
    }
    public ByteArrayInputStream exportUsersToExcel() throws IOException {
        List<User> users = getAllusers();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            // Header
            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"Name", "Username", "Password", "Email", "Roles"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Data
            int rowIdx = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(user.getName());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getPassword());
                row.createCell(3).setCellValue(user.getEmail());

                String roles = user.getRoles().stream()
                        .map(Role::getName)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
                row.createCell(4).setCellValue(roles);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
    public List<User> readUsersFromExcel(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

        List<User> users = new ArrayList<>();

        // Duyệt qua từng dòng của sheet, bắt đầu từ dòng thứ 1 (dòng tiêu đề đã có trong mẫu)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row currentRow = sheet.getRow(i);
            if (currentRow != null) {
                String username = currentRow.getCell(1).getStringCellValue();
                Optional<User> existingUserOptional = Optional.ofNullable(userRepository.findByUsername(username));

                // Kiểm tra password
                String password = "";
                if (currentRow.getCell(2) != null) {
                    password = currentRow.getCell(2).getStringCellValue();
                }

                if (password.isEmpty()) {
                    password = username; // Set password thành username nếu password để trống
                }

                if (existingUserOptional.isPresent()) {
                    User existingUser = existingUserOptional.get();
                    // Nếu user đã tồn tại, bỏ qua không add user này và tiếp tục với user tiếp theo
                    continue;
                } else {
                    User user = new User();
                    user.setName(currentRow.getCell(0).getStringCellValue());
                    user.setUsername(username);

                    // Kiểm tra xem password có phải đoạn mã hóa hay không
                    if (password.startsWith("$2a$")) {
                        user.setPassword(password); // Giữ nguyên đoạn mã hóa
                    } else {
                        user.setPassword(new BCryptPasswordEncoder().encode(password)); // Mã hóa password mới
                    }

                    user.setEmail(currentRow.getCell(3).getStringCellValue());

                    // Xử lý vai trò từ file Excel
                    String[] roleNames = currentRow.getCell(4).getStringCellValue().split(",");
                    Set<Role> roles = new HashSet<>();
                    for (String roleName : roleNames) {
                        Role role = rolePepository.findByName(roleName.trim());
                        if (role != null) {
                            roles.add(role);
                        }
                    }
                    user.setRoles(new ArrayList<>(roles));

                    users.add(user);
                }
            }
        }

        workbook.close();
        return users;
    }
    public void updateResetPasswordToken(String token, String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("Không tìm thấy người dùng với email: " + email));
        user.setResetToken(token);
        userRepository.save(user);
    }

    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetToken(token).orElse(null);
    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetToken(null);
        userRepository.save(user);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUserLoginDetails(User user) {
        // Cập nhật thông tin đăng nhập nếu cần thiết
        userRepository.save(user);
    }
    public void updateActiveStatus(Long userId, boolean active) {
        userRepository.updateActiveStatus(userId, active);
    }
}