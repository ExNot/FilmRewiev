package com.example.demo.Controller;

import com.example.demo.Model.Film;
import com.example.demo.Model.Relation.UserRating;
import com.example.demo.Model.User;
import com.example.demo.Repository.FilmRepository;
import com.example.demo.Repository.UserRatingRepository;
import com.example.demo.Service.CustomUserDetails;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserRatingRepository userRatingRepository;
    public UserController(UserService userService, UserRatingRepository userRatingRepository) {
        this.userService = userService;
        this.userRatingRepository = userRatingRepository;
    }

    @Autowired
    private HttpSession session;

    @GetMapping("/{username}")
    public String userInfo(@PathVariable String username, Model model){
        Optional<User> user = userService.getUserByUserName(username);
        if (user.isPresent()){
            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String currentUsername = customUserDetails.getUsername();
            if (user.get().getUsername().equals(currentUsername)){
                List<UserRating> userRatings = userRatingRepository.findByUser_Id(user.get().getId());

                userRatings = userRatings.stream()
                        .sorted(Comparator.comparing(UserRating::getRating).reversed())
                                .collect(Collectors.toList());

                /*List<Film> ratedFilms = userRatings.stream()
                        .map(userRating -> userRating.getFilm())
                        .collect(Collectors.toList());*/


               /* List<Film> sortedFilms = ratedFilms.stream()
                                .sorted(Comparator.comparing(film -> film.getUserRatings().size(), Comparator.reverseOrder()))
                                        .collect(Collectors.toList());

                for (int i = 0; i< sortedFilms.size(); i++){
                    System.out.println(sortedFilms.get(i).getUserRatings());
                }*/
                model.addAttribute("userRatings", userRatings);
                user.ifPresent(value -> model.addAttribute("user", value));

            }else {
                model.addAttribute("warningMessage", "You are trying to view another user's profile !!!!!");
            }
        }

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


    @PostMapping("/updateProfilePhoto")
    public String updateProfilePhoto(@RequestParam("newPPUrl") String newPPUrl) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userService.getUserByUserName(userDetails.getUsername());
        if (user.isPresent()) {

            User currentUser = user.get();
            currentUser.setPPUrl(newPPUrl);
            userService.saveUser(currentUser);

            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            customUserDetails.setPPUrl(newPPUrl);

            return "redirect:/films";
        }

        // Kullanıcı bulunamazsa eroor döndür
        return "ERROR NOT FOUND USER";
    }


}

















