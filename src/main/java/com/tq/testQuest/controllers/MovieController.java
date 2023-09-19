package com.tq.testQuest.controllers;

import com.tq.testQuest.models.FavoriteMovie;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.models.User;
import com.tq.testQuest.services.MovieService;
import com.tq.testQuest.services.MovieServiceImpl;
import com.tq.testQuest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final UserService userService;
    private final MovieService movieService;

    @Autowired
    public MovieController(UserService userService, MovieService movieService, MovieServiceImpl movieServiceImpl) {
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
    public ResponseEntity<String> addToFavorites(Authentication authentication, @RequestBody Long movieId) {
        Movie movie = movieService.findById(movieId);
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (movie != null && user != null) {
            movieService.addToFavorites(user, movie);
            return ResponseEntity.ok("Фильм успешно добавлен в избранное");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Фильм или пользователь не найден");
        }
    }


    @DeleteMapping("/favourites")
    public ResponseEntity<String> removeFromFavorites(Authentication authentication, @RequestBody Long movieId) {
        Movie movie = movieService.findById(movieId);
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (movie == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Фильм или пользователь не найден");
        }
        List<FavoriteMovie> existingFavorite = movieService.getFavoriteMovies(authentication);
        if (existingFavorite == null) {
            return ResponseEntity.badRequest().body("Фильм не найден в избранном");
        }
        movieService.removeFromFavorites(authentication);
        return ResponseEntity.ok("Фильм успешно удален из избранного");
    }

    @GetMapping("/nonFavoriteMovies")
    public ResponseEntity<List<Movie>> getNonFavoriteMovies(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int perPage
    ) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Pageable pageable = PageRequest.of(page - 1, perPage);
        List<Movie> nonFavoriteMovies = movieService.getNonFavoriteMovies(authentication, pageable);
        if (nonFavoriteMovies == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(nonFavoriteMovies);
    }
    @GetMapping("/favorites")
    public ResponseEntity<List<Movie>> getFavoriteMovies(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<FavoriteMovie> favoriteMovies = movieService.getFavoriteMovies(authentication);

        List<Movie> favoriteMovieList = favoriteMovies.stream()
                .map(FavoriteMovie::getMovie)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favoriteMovieList);
    }

}