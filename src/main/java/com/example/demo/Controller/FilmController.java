package com.example.demo.Controller;

import com.example.demo.Model.Film;
import com.example.demo.Model.Relation.UserRating;
import com.example.demo.Repository.UserRatingRepository;
import com.example.demo.Service.CustomUserDetails;
import com.example.demo.Service.FilmService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final UserRatingRepository userRatingRepository;


    public FilmController(FilmService filmService, UserRatingRepository userRatingRepository) {
        this.filmService = filmService;this.userRatingRepository = userRatingRepository;
    }

    @GetMapping
    public String filmList(Model model) throws IOException {
        List<Film> films = filmService.getAllFilms();
        model.addAttribute("films", films);
        return "FilmTemplates/film-list";
    }

    @GetMapping("/discover")
    public String index(){
        return "discover";
    }


    @GetMapping("/{id}")
    public String filmDetail(@PathVariable Long id, Model model){
        Optional<Film> film = filmService.getFilmById(id);
        film.ifPresent(value -> model.addAttribute("film", value));

        //SEND PREVIOUS STAR COUNT TO HTML!
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserRating userRating = userRatingRepository.findByUser_IdAndFilm_Id(customUserDetails.getId(), id);
        if (userRating != null){
            model.addAttribute("filmRating", userRatingRepository.findByUser_IdAndFilm_Id(customUserDetails.getId(), id).getRating());
        }
        else{
            model.addAttribute("filmRating", 0);
        }



        return "FilmTemplates/film-detail";
    }

    @GetMapping("/add")
    public String addFilmForm(Model model){
        model.addAttribute("newFilm", new Film());
        return "FilmTemplates/film-add";
    }
    @PostMapping("/add")
    public String addFilm(@ModelAttribute Film newFilm){
        filmService.saveFilm(newFilm);
        return "redirect:/films";
    }

    @PostMapping("/delete/{id}")
    public String deleteFilm(@PathVariable Long id){
        filmService.deleteFilm(id);
        return "redirect:/films";

    }

    @PostMapping("/rate")
    public String rateFilm(@RequestParam("filmId") Long filmId, Model model,@RequestParam double stars){
        filmService.rateFilm(filmId, stars);
        return "redirect:/films";
    }
}




























