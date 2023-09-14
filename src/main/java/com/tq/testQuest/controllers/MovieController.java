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
import java.util.Optional;

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
            @RequestParam(defaultValue = "5") int perPage
    ) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<Movie> savedMoviesPage = movieService.getAllMovies(pageable);

        return ResponseEntity.ok(savedMoviesPage.getContent());
    }

    @PostMapping("/favourites")
    public ResponseEntity<String> addToFavorites(@RequestHeader("User-Id") Long userId, @RequestBody Long movieId) {
       Movie movie = movieService.findById(movieId);
       User user = userService.getUserById(userId);
       movieService.addToFavorites(user,movie);
       return ResponseEntity.ok("Фильм успешно добавлен в избранное");
    }

    @DeleteMapping("/favourites")
    public ResponseEntity<String> removeFromFavorites(@RequestHeader("User-Id") Long userId, @RequestBody Long movieId) {
        Movie movie = movieService.findById(movieId);
        User user = userService.getUserById(userId);
        if (movie == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Фильм или пользователь не найден");
        }
        FavoriteMovie existingFavorite = movieService.getFavoriteMovie(user, movie);
        if (existingFavorite == null) {
            return ResponseEntity.badRequest().body("Фильм не найден в избранном");
        }
        movieService.removeFromFavorites(user, movie);
        return ResponseEntity.ok("Фильм успешно удален из избранного");
    }

    @GetMapping("/nonFavoriteMovies")
    public ResponseEntity<List<Movie>> getNonFavoriteMovies(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int perPage,
            @RequestParam Long movieId
    ) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Pageable pageable = PageRequest.of(page - 1, perPage);
        List<Movie> nonFavoriteMovies = movieService.getNonFavoriteMovies(userId);
        if (nonFavoriteMovies == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(nonFavoriteMovies);
    }
}
