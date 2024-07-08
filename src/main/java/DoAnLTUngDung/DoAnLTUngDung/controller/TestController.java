package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Role;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.IRoleRepository;
import DoAnLTUngDung.DoAnLTUngDung.services.RoleService;
import DoAnLTUngDung.DoAnLTUngDung.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TestController {
    @Autowired
    private RoleService roleService;
    private UserServices userService;
    private IRoleRepository roleRepository;

    @GetMapping("/role/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        User user = userService.getUsersById(id);
        //List<Role> roles = roleRepository.findAllRoles();
        if (user != null) {
            model.addAttribute("user", user);
          //  model.addAttribute("roles", roles);
            return "ADMIN/role";
        }
        return "redirect:/userlist";
    }
}
