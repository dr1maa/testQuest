package com.tq.testQuest.repositories;

import com.tq.testQuest.models.FavoriteMovie;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteMovieRepository extends JpaRepository<FavoriteMovie, Long> {

    FavoriteMovie findByUserAndMovie(User user, Movie movie);
    List<FavoriteMovie> findByUser(User user);
}
