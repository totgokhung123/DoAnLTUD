package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.Role;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.IRoleRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IUserRepository;
import DoAnLTUngDung.DoAnLTUngDung.services.RoleService;
import DoAnLTUngDung.DoAnLTUngDung.services.UserService;
import DoAnLTUngDung.DoAnLTUngDung.services.UserServices;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import  DoAnLTUngDung.DoAnLTUngDung.config.Utility;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;



@Controller
public class UserController {
    @Autowired
    private UserServices userService;

    private IUserRepository userRepository;
    @Autowired
    private RoleService roleService;

    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private JavaMailSender mailSender;
    @GetMapping("/auth-login-basic")
    public String Login()
    {
        return "Login/auth-login-basic";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "Login/auth-register-basic";
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
            return "Login/auth-register-basic";
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.save(user);
        return "redirect:/index";
    }
    @GetMapping("/userlist")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public String Userlist( @RequestParam(name = "name", required = false) String name,
                            @RequestParam(name = "username", required = false) String username,
                            @RequestParam(name = "email", required = false) String email,
                            @RequestParam(name = "accountNonLocked", required = false) Boolean accountNonLocked
                            ,Model model) {
        List<User> userList = userService.getAllusers();
        List<Role> roles = roleService.findAllRoles();
        List<User> users = userService.searchUsers(name, username, email, accountNonLocked);
        model.addAttribute("DSUser", users);
        model.addAttribute("roles", roles);
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
            return "ADMIN/editUser";
        }
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
            return "ADMIN/editUser";
        }
        // Cập nhật thông tin user
        editedUser.setPassword(new BCryptPasswordEncoder().encode(editedUser.getPassword()));
        userService.edit(editedUser);
        return "redirect:/userlist";
    }
    @PostMapping("/changeRole")
    public String changeUserRole(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId, Model model) {
        userService.changeUserRole(userId, roleId);
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
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "ADMIN/forgotpassword";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = UUID.randomUUID().toString();

        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset-password?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "Chúng tôi đã gửi liên kết đặt lại mật khẩu tới email của bạn. Vui lòng kiểm tra.");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "ADMIN/forgotpassword";
    }

    private void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("chutienbinh2003@gmail.com", "Totgokhung123 🤖");
        helper.setTo(recipientEmail);

        String subject = "Liên kết đặt lại mật khẩu của bạn";

        String content = "<p>Xin chào ✨</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu của mình.</p>"
                + "<p>Nhấn vào liên kết bên dưới để thay đổi mật khẩu của bạn:</p>"
                + "<p><a href=\"" + link + "\">Reset Password here</a></p>"
                + "<br>"
                + "<p>Bỏ qua email này nếu bạn nhớ mật khẩu của mình, "
                + "hoặc bạn không thực hiện yêu cầu này.</p>";

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "Login/auth-login-basic";
        }

        return "ADMIN/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        User user = userService.getByResetPasswordToken(token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "Login/auth-login-basic";
        } else if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match");
            return "ADMIN/reset-password";
        } else {
            userService.updatePassword(user, password);
            model.addAttribute("message", "You have successfully reset your password.");
        }

        return "Login/auth-login-basic";
    }
    @Autowired
    private UserService u;
    @GetMapping("/toggle-user-lock-status")
    public String toggleUserLockStatus(@RequestParam("id") Long userId, RedirectAttributes redirectAttributes) {
        try {
            u.toggleUserLockStatus(userId);
            redirectAttributes.addFlashAttribute("message", "Đã thay đổi trạng thái tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi khi thay đổi trạng thái tài khoản: " + e.getMessage());
        }
        return "redirect:/userlist";
    }
}
