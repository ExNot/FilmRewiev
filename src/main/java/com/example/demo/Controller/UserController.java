package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Service.UserService;
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

    @GetMapping("/{id}")
    public String userInfo(@PathVariable Long id, Model model){
        Optional<User> user = userService.getUserById(id);
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

    @PostMapping("/login")
    public String directAfterLogin(@ModelAttribute("user") User user, Model model){
        if (userService.authenticateUser(user.getUsername(), user.getPassword())){
            return "redirect:/films";
        } else{
            model.addAttribute("error", "Invalid username or password");
            return "UserTemplates/login";
        }
    }


}

















