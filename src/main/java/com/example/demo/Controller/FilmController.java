package com.example.demo.Controller;

import com.example.demo.Model.Film;
import com.example.demo.Model.User;
import com.example.demo.Service.CustomUserDetails;
import com.example.demo.Service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    @Autowired
    private UserDetailsService userDetailsService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public String filmList(Model model){
        List<Film> films = filmService.getAllFilms();
        model.addAttribute("films", films);

        /*CustomUserDetails customUserDetails =(CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("ppUrl", customUserDetails.getPPUrl());
        model.addAttribute("userId", customUserDetails.getId());
        model.addAttribute("username", customUserDetails.getUsername());*/
        return "FilmTemplates/film-list";
    }
    @GetMapping("/{id}")
    public String filmDetail(@PathVariable Long id, Model model){
        Optional<Film> film = filmService.getFilmById(id);
        film.ifPresent(value -> model.addAttribute("film", value));
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
}




























