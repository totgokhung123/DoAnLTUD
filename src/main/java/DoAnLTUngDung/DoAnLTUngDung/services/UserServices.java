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
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServices {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository rolePepository;
    public void save(User user) {

        userRepository.save(user);
        Long userId = userRepository.getUserIdByUsername(user.getUsername());
        Long roleId = rolePepository.getRoleIdByName("USER");
        if(roleId != 0 && userId != 0){
            userRepository.addRoleToUser(userId,roleId);
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


    //exxuat

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

    //nhapex
    public List<User> readUsersFromExcel(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

        List<User> users = new ArrayList<>();

        // Duyệt qua từng dòng của sheet, bắt đầu từ dòng thứ 1 (dòng tiêu đề đã có trong mẫu)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row currentRow = sheet.getRow(i);
            if (currentRow != null) {
                User user = new User();
                user.setName(currentRow.getCell(0).getStringCellValue());
                user.setUsername(currentRow.getCell(1).getStringCellValue());
                user.setPassword(currentRow.getCell(2).getStringCellValue());
                user.setEmail(currentRow.getCell(3).getStringCellValue());

                // Xử lý vai trò từ file Excel
                String roleName = currentRow.getCell(4).getStringCellValue();
                Role role = rolePepository.findByName(roleName);
                if (role == null) {
                    // Xử lý nếu vai trò không tồn tại
                    throw new RuntimeException("Vai trò '" + roleName + "' không tồn tại.");
                }

                //user.addRole(role);

                users.add(user);
            }
        }

        workbook.close();
        return users;
    }
}