package com.tq.testQuest.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tq.testQuest.models.FavoriteMovie;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.models.User;
import com.tq.testQuest.repositories.FavoriteMovieRepository;
import com.tq.testQuest.repositories.MovieRepository;
import com.tq.testQuest.repositories.UserRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final Set<String> savedMovieTitles;
    @Autowired
    private final FavoriteMovieRepository favoriteMovieRepository;

    public MovieController(MovieRepository movieRepository, UserRepository userRepository, OkHttpClient client, ObjectMapper objectMapper, FavoriteMovieRepository favoriteMovieRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.client = client;
        this.objectMapper = objectMapper;
        this.favoriteMovieRepository = favoriteMovieRepository;
        this.savedMovieTitles = new HashSet<>();
    }

    @Scheduled(fixedRate = 10800000)
    void fetchMoviesAndSave() throws IOException {
        for (int page = 1; page <= 5; page++) {
            fetchAndSaveMoviesFromPage(page);
        }
    }

    public void saveMovieToDatabase(String title, String posterPath) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setPosterPath(posterPath);
        movieRepository.save(movie);
    }

    private void fetchAndSaveMoviesFromPage(int page) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmZTgwN2Y3Zjg1OThkNjZhMDZlY2Y3NTRiZGY5ZWUxZCIsInN1YiI6IjY0ZjIzNjBiZTBjYTdmMDBhZTM5YmRiYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.UFkHevBZYjleei8IIez043l0kHUK8s2Eenxu-_4tt7c")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            saveMoviesFromResponse(responseBody);
        } else {
            System.err.println("Ошибка выполнения запроса" + response.code());
        }
    }

    private void saveMoviesFromResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode results = jsonNode.get("results");
            for (JsonNode movieNode : results) {
                String title = movieNode.get("title").asText();
                String posterPath = movieNode.get("poster_path").asText();
                if (!savedMovieTitles.contains(title)) {
                    savedMovieTitles.add(title);
                    saveMovieToDatabase(title, posterPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/savedMovies")
    public ResponseEntity<List<Movie>> getSavedMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int perPage
    ) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<Movie> savedMoviesPage = movieRepository.findAll(pageable);

        return ResponseEntity.ok(savedMoviesPage.getContent());
    }

    @PostMapping("/addToFavorites")
    public ResponseEntity<String> addToFavorites(@RequestHeader("User-Id") Long userId, @RequestBody Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (movie == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Фильм или пользователь не найден");
        }
        FavoriteMovie existingFavorite = favoriteMovieRepository.findByUserAndMovie(user, movie);
        if (existingFavorite != null) {
            return ResponseEntity.badRequest().body("Фильм уже добавлен в избранное");
        }
        FavoriteMovie favoriteMovie = new FavoriteMovie();
        favoriteMovie.setUser(user);
        favoriteMovie.setMovie(movie);
        favoriteMovieRepository.save(favoriteMovie);
        return ResponseEntity.ok("Фильм успешно добавлен в избранное");
    }

    @DeleteMapping("/removeFromFavorites")
    public ResponseEntity<String> removeFromFavorites(@RequestHeader("User-Id") Long userId, @RequestBody Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (movie == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Фильм или пользователь не найден");
        }
        FavoriteMovie existingFavorite = favoriteMovieRepository.findByUserAndMovie(user, movie);
        if (existingFavorite == null) {
            return ResponseEntity.badRequest().body("Фильм не найден в избранном");
        }
        favoriteMovieRepository.delete(existingFavorite);
        return ResponseEntity.ok("Фильм успешно удален из избранного");
    }

    @GetMapping("/nonFavoriteMovies")
    public ResponseEntity<List<Movie>> getNonFavoriteMovies(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int perPage,
            @RequestParam String loaderType
    ) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Pageable pageable = PageRequest.of(page - 1, perPage);
        if ("sql".equals(loaderType)) {
            // Реализуйте логику загрузки фильмов с использованием SQL-запроса
            Page<Movie> nonFavoriteMoviesPage = movieRepository.findNonFavoriteMovies(user, pageable);
            return ResponseEntity.ok(nonFavoriteMoviesPage.getContent());
        } else if ("inMemory".equals(loaderType)) {
            // Реализуйте логику загрузки фильмов в памяти приложения
            List<Movie> allMovies = movieRepository.findAll();
            List<FavoriteMovie> favoriteMovies = favoriteMovieRepository.findByUser(user);
            List<Movie> nonFavoriteMovies = allMovies.stream()
                    .filter(movie -> favoriteMovies.stream()
                            .noneMatch(favoriteMovie -> favoriteMovie.getMovie().equals(movie)))
                    .collect(Collectors.toList());
            int start = Math.min((page - 1) * perPage, nonFavoriteMovies.size());
            int end = Math.min(start + perPage, nonFavoriteMovies.size());
            return ResponseEntity.ok(nonFavoriteMovies.subList(start, end));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
