package com.tq.testQuest.services;

import com.tq.testQuest.models.FavoriteMovie;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface MovieService {

    Movie saveMovie(String title, String posterPath);

    Page<Movie> getAllMovies(Pageable pageable);


    Movie findById(Long movieId);

    List<FavoriteMovie> getFavoriteMovies(Authentication authentication);

    List<Movie> getNonFavoriteMovies(Authentication authentication);

    void addToFavorites(User user, Movie movie);

    void removeFromFavorites(Authentication authentication);
}