package com.tq.testQuest.controllers;

import com.tq.testQuest.models.FavoriteMovie;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.models.User;
import com.tq.testQuest.services.MovieService;
import com.tq.testQuest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final UserService userService;
    private final MovieService movieService;

    @Autowired
    public MovieController(UserService userService, MovieService movieService) {
        this.userService = userService;
        this.movieService = movieService;
    }

    @GetMapping("/savedMovies")
    public ResponseEntity<List<Movie>> getSavedMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int perPage
    ) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<Movie> savedMoviesPage = movieService.getAllMovies(pageable);

        return ResponseEntity.ok(savedMoviesPage.getContent());
    }

    @PostMapping("/favourites post")
    public ResponseEntity<String> addToFavorites(@RequestHeader("User-Id") User userId, @RequestBody Long movieId) {
        Movie movie = movieService.findById(movieId);
        User user = userService.findById(userId);
        if (movie == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Фильм или пользователь не найден");
        }
        FavoriteMovie existingFavorite = movieService.findFavoriteMovie(user, movie);
        if (existingFavorite != null) {
            return ResponseEntity.badRequest().body("Фильм уже добавлен в избранное");
        }
        movieService.addToFavorites(user, movie);
        return ResponseEntity.ok("Фильм успешно добавлен в избранное");
    }

    @DeleteMapping("/favourites delete")
    public ResponseEntity<String> removeFromFavorites(@RequestHeader("User-Id") User userId, @RequestBody Long movieId) {
        Movie movie = movieService.findById(movieId);
        User user = userService.findById(userId);
        if (movie == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Фильм или пользователь не найден");
        }
        FavoriteMovie existingFavorite = movieService.findFavoriteMovie(user, movie);
        if (existingFavorite == null) {
            return ResponseEntity.badRequest().body("Фильм не найден в избранном");
        }
        movieService.removeFromFavorites(user, movie);
        return ResponseEntity.ok("Фильм успешно удален из избранного");
    }

    @GetMapping("/nonFavoriteMovies")
    public ResponseEntity<List<Movie>> getNonFavoriteMovies(
            @RequestHeader("User-Id") User userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int perPage,
            @RequestParam String loaderType
    ) {
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Pageable pageable = PageRequest.of(page - 1, perPage);
        List<Movie> nonFavoriteMovies = movieService.getNonFavoriteMovies(userId, pageable, loaderType);
        if (nonFavoriteMovies == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(nonFavoriteMovies);
    }
}
