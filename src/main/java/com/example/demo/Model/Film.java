package com.example.demo.Model;

import com.example.demo.Model.Relation.UserRating;
import jakarta.persistence.*;

import java.time.Year;
import java.util.List;

@Entity
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String director;
    private Year releaseDate;
    private String description;
    private Double IMDBRating;
    @OneToMany(mappedBy = "film")
    private List<UserRating> userRatings;




    public List<UserRating> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(List<UserRating> userRatings) {
        this.userRatings = userRatings;
    }

    public Double getIMDBRating() {
        return IMDBRating;
    }

    public void setIMDBRating(Double IMDBRating) {
        this.IMDBRating = IMDBRating;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Year getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Year releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
