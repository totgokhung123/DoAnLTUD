package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Role;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.IUserRepository;
import DoAnLTUngDung.DoAnLTUngDung.services.EmailService;
import DoAnLTUngDung.DoAnLTUngDung.services.RoleService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;


//    @PostMapping("/forgot-password")
//    public String forgotPassword(@RequestParam String email) throws MessagingException {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            String token = UUID.randomUUID().toString();
//            user.setResetToken(token);
//            userRepository.save(user);
//
//            String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
//            emailService.sendEmail(user.getEmail(), "Password Reset Request", "To reset your password, click the link below:\n" + resetLink);
//
//            return "Password reset email sent.";
//        } else {
//            return "User not found.";
//        }
//    }
//    @GetMapping("/reset-password")
//    public String showResetPasswordPage(@RequestParam String token) {
//        Optional<User> userOptional = userRepository.findByResetToken(token);
//        return userOptional.isPresent() ? "Reset Password Form" : "Invalid token";
//    }
//
//    @PostMapping("/reset-password")
//    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
//        Optional<User> userOptional = userRepository.findByResetToken(token);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            user.setPassword(passwordEncoder.encode(newPassword));
//            user.setResetToken(null);
//            userRepository.save(user);
//            return "Password successfully reset.";
//        } else {
//            return "Invalid token";
//        }
//    }

    @GetMapping("/login-success")
    public String loginSuccess(OAuth2AuthenticationToken authentication) {
        OAuth2User oauthUser = authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        // Kiểm tra xem người dùng đã tồn tại trong cơ sở dữ liệu chưa
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            // Người dùng đã tồn tại, cập nhật thông tin nếu cần thiết
            User user = existingUser.get();
            user.setName(oauthUser.getAttribute("name"));
            userRepository.save(user);
        } else {
            // Người dùng mới, tạo người dùng mới
            User newUser = new User();
            newUser.setName(oauthUser.getAttribute("name"));
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Đặt mật khẩu ngẫu nhiên
            newUser.setUsername(email);
            // Thiết lập vai trò cho người dùng mới
            Role defaultRole = roleService.findRoleByName("USER");
            if (defaultRole != null) {
                newUser.setRoles(List.of(defaultRole));
            }
            userRepository.save(newUser);
        }

        return "Login/auth-login-basic";
    }
}