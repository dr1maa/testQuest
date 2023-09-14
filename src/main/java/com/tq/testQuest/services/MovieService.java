package com.tq.testQuest.services;

import com.tq.testQuest.models.FavoriteMovie;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MovieService {

    Movie saveMovie(String title, String posterPath);

    Page<Movie> getAllMovies(Pageable pageable);

    Movie deleteMovie(Long movieId);

    Movie findById(Long movieId);

    FavoriteMovie getFavoriteMovie(User user, Movie movie);

    List<Movie> getNonFavoriteMovies(Long userId);


    void addToFavorites(User user, Movie movie);

    void removeFromFavorites(User user, Movie movie);
}