package com.example.demo.Repository;

import com.example.demo.Model.Film;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {
    List<Film> findByNameContainingIgnoreCase(String name);
}
