package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.CustomOAuth2User;
import DoAnLTUngDung.DoAnLTUngDung.entity.Role;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserServices userService;
    private RoleService roleService;

    public OAuth2LoginSuccessHandler(UserServices userService) {
        this.userService = userService;
    }
    @Autowired
    private IUserRepository userRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();

        Optional<User> optionalUser = userService.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userService.updateUserLoginDetails(user);
        } else {
            User newUser = new User();
            newUser.setName(oAuth2User.getName());
            newUser.setEmail(email);
            newUser.setUsername(email);
            newUser.setPassword(new BCryptPasswordEncoder().encode(email));
//            Role role = new Role();
//            role.setName("USER");
//            newUser.setRoles(Collections.singletonList(role)); // or any default roles
            // Thiết lập vai trò cho người dùng mới
//            Role defaultRole = roleService.findRoleByName("USER");
//            if (defaultRole != null) {
//                newUser.setRoles(List.of(defaultRole));
//            }
            userService.save(newUser);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
        FlashMap flashMap = new FlashMap();
        flashMap.put("successMessage", "Đăng nhập thành công với Google!");
        flashMapManager.saveOutputFlashMap(flashMap, request, response);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}