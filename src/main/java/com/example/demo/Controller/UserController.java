package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private HttpSession session;

    @GetMapping("/{username}")
    public String userInfo(@PathVariable String username, Model model){
        Optional<User> user = userService.getUserByUserName(username);
        user.ifPresent(value -> model.addAttribute("user", value));
        return "UserTemplates/user-info";
    }

    @GetMapping("/register")
    public String regForm(Model model){
        model.addAttribute("newUser", new User());
        return "UserTemplates/register";
    }
    @PostMapping("/register")
    public String regUser(@ModelAttribute User newUser){
        userService.saveUser(newUser);
        Long id = newUser.getId();
        return "redirect:/user/" + id;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model){
        model.addAttribute("user", new User());
        return "UserTemplates/login";
    }

    /*@PostMapping("/login")
    public String directAfterLogin(@ModelAttribute("user") User user, Model model){
        session.setAttribute("loggedIn", true);
        if (userService.authenticateUser(user.getUsername(), user.getPassword())){
            session.setAttribute("loggedIn", true);
            return "redirect:/films";
        } else{
            model.addAttribute("error", "Invalid username or password");
            return "UserTemplates/login";
        }


    }
*/

    @PostMapping("/login")
    public String directAfterLogin(@ModelAttribute("user") User user, Model model){
            session.setAttribute("loggedIn", true);
            model.addAttribute("loggedIn", true);
            return "redirect:/films";

    }


}

















