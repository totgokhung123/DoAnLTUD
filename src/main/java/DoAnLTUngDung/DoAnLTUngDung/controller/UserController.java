package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserServices userService;

    @GetMapping("/auth-login-basic")
    public String Login()
    {
        return "html/auth-login-basic";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "html/auth-register-basic";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                model.addAttribute(error.getField() + "_error",
                        error.getDefaultMessage());
            }
            return "html/auth-register-basic";
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.save(user);
        return "redirect:/auth-login-basic";
    }
    @GetMapping("/userlist")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public String Userlist(Model model) {
        List<User> userList = userService.getAllusers();
        model.addAttribute("DSUser", userList);
        return "ADMIN/DSUser";
    }
    @GetMapping("/userlist/add")
    public String add(Model model) {
        model.addAttribute("user", new User());
        return "ADMIN/addUser";
    }

    @PostMapping("/userlist/add")
    public String add(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                model.addAttribute(error.getField() + "_error",
                        error.getDefaultMessage());
            }
            return "ADMIN/addUser";
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.save(user);
        return "redirect:/userlist";
    }
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        User user = userService.getUsersById(id);
        if (user != null) {
            model.addAttribute("user", user);
            return "ADMIN/editUser"; // Thay đổi đường dẫn và tên file thích hợp
        }
        // Xử lý trường hợp không tìm thấy user
        return "redirect:/userlist";
    }

    @PostMapping("/edit")
    public String editUser(@Valid @ModelAttribute("user") User editedUser,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                model.addAttribute(error.getField() + "_error",
                        error.getDefaultMessage());
            }
            return "ADMIN/editUser"; // Thay đổi đường dẫn và tên file thích hợp
        }
        // Cập nhật thông tin user
        editedUser.setPassword(new BCryptPasswordEncoder().encode(editedUser.getPassword()));
        userService.edit(editedUser);
        return "redirect:/userlist";
    }
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        userService.deleteUsers(id);
        return "redirect:/userlist";
    }
    // Thêm phương thức để xóa nhiều người dùng
    @PostMapping("/delete-multiple")
    public String deleteMultipleUsers(@RequestParam("userIds") List<Long> userIds) {
        userService.deleteMultipleUsers(userIds);
        return "redirect:/userlist";
    }

    //nhapex
    @PostMapping("/import-users")
    public String importUsers(@RequestParam("file") MultipartFile file) {
        try {
            // Kiểm tra file có tồn tại và định dạng hợp lệ
            if (file.isEmpty()) {
                // Xử lý khi file không tồn tại
                return "redirect:/userlist?import_error=empty";
            }
            // Xử lý đọc dữ liệu từ file Excel và thêm người dùng vào cơ sở dữ liệu
            List<User> users = userService.readUsersFromExcel(file.getInputStream());
            for (User user : users) {
                // Lưu hoặc thêm user vào cơ sở dữ liệu
                userService.save(user);
            }
            return "redirect:/userlist";
        } catch (Exception e) {
            // Xử lý ngoại lệ nếu có lỗi khi đọc hoặc lưu dữ liệu
            return "redirect:/userlist?import_error=" + e.getMessage();
        }
    }


    //xuatex
    @GetMapping("/export-users")
    public ResponseEntity<InputStreamResource> exportToExcel() throws IOException {
        ByteArrayInputStream in = userService.exportUsersToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }
}
