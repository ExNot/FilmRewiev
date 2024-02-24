package com.example.demo.Model;

import com.example.demo.Model.Relation.UserRating;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;
    private String PPUrl;

    @OneToMany(mappedBy = "user")
    private List<UserRating> userRatings;

    public List<UserRating> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(List<UserRating> userRatings) {
        this.userRatings = userRatings;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPPUrl() {
        return PPUrl;
    }

    public void setPPUrl(String PPUrl) {
        this.PPUrl = PPUrl;
    }
}
