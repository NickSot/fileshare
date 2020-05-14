package org.tues.fileshare.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.tues.fileshare.Entity.User;
import org.tues.fileshare.Service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model, HttpServletRequest request){
        HttpSession session  = request.getSession();
        Object attr = session.getAttribute("username");
        String username;
        User user = null;

        if (attr == null){
            username = "traveller";
        }
        else{
            username = attr.toString();
            user = userService.findByUsername(username);
        }

        model.addAttribute("username", username);
        model.addAttribute("user", user);
        return "home";
    }
}
