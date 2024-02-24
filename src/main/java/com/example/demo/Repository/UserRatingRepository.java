package com.example.demo.Repository;

import com.example.demo.Model.Relation.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    UserRating findByUser_IdAndFilm_Id(Long userId, Long filmId);
}
