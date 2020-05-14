package org.tues.fileshare.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.tues.fileshare.Entity.User;
import org.tues.fileshare.Entity.Validator.UserValidator;
import org.tues.fileshare.Service.IUserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UsersController {
    @Autowired
    private IUserService userService;
    private String STORAGE_PATH = "C:\\Users\\NIKI\\Desktop\\fileshare\\src\\main\\resources\\static\\storage\\";

    @GetMapping("/register")
    public String register(Model model, HttpServletRequest request){
        if (request.getSession().getAttribute("username") != null){
            return "redirect:/";
        }

        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute User user, BindingResult result, HttpServletRequest request){
        if (request.getSession().getAttribute("username") != null){
            return "redirect:/";
        }

        UserValidator userValidator = new UserValidator();
        userValidator.validate(user, result);

        if (result.hasErrors()){
            return "register";
        }

        userService.save(user);

        Path path = Paths.get(STORAGE_PATH + user.getUsername());

        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request){
        if (request.getSession().getAttribute("username") != null){
            return "redirect:/";
        }

        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String logUserIn(@ModelAttribute User user, HttpServletRequest request, HttpServletResponse response, BindingResult result){
        User u = userService.findByUsername(user.getUsername());

        if (u == null){
            return "login";
        }

        if (!user.getPassword().equals(u.getPassword())){
            return "login";
        }

        HttpSession session = request.getSession();
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logUserOut(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();

        return "redirect:/";
    }
}
