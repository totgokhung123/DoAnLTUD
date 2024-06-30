package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    @PreAuthorize("hasAuthority('ADMIN')")
    public String Userlist(Model model) {
        List<User> userList = userService.getAllusers();
        model.addAttribute("DSUser", userList);
        return "ADMIN/DSUser";
    }
    @GetMapping("/userlist/edit/{id}")
    public String EditUser(@PathVariable("id") Long id, Model model) {
        User user = userService.getUsersById(id);
        if (user != null) {
            model.addAttribute("user", user);
            return "ADMIN/editUser";
        }
        return "redirect:/userlist";
    }

    @PostMapping("/userlist/edit/{id}")
    public String updateUser(@PathVariable("id") Long id, @Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "ADMIN/editUser";
        }
        userService.saveUsers(user);
        return "redirect:/userlist";
    }

}
