package DoAnLTUngDung.DoAnLTUngDung.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/")
public class HomeController {
    @GetMapping("/")
    public String home(){
        return "html/auth-login-basic";
    }
    @GetMapping("/home")
    public String test(){
        return "html/index";
    }
}
