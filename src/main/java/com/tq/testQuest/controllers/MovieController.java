package com.tq.testQuest.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.repositories.MovieRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private final MovieRepository movieRepository;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final Set<String> savedMovieTitles; // Для отслеживания сохраненных названий фильмов

    public MovieController(MovieRepository movieRepository, OkHttpClient client, ObjectMapper objectMapper) {
        this.movieRepository = movieRepository;
        this.client = client;
        this.objectMapper = objectMapper;
        this.savedMovieTitles = new HashSet<>();
    }

        // Запланированный метод для получения фильмов каждые 3 часа
    @Scheduled(fixedRate = 3 * 60 * 60 * 1000) // 3 часа в миллисекундах
    void fetchMoviesAndSave() throws IOException {
        for (int page = 1; page <= 5; page++) {
            fetchAndSaveMoviesFromPage(page);
        }
    }
public void saveMovie(String title,String posterPath){
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setPosterPath(posterPath);
        movieRepository.save(movie);
}
    private void fetchAndSaveMoviesFromPage(int page) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=" + page + "&sort_by=popularity.desc")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmZTgwN2Y3Zjg1OThkNjZhMDZlY2F7NTRiZGY5ZWUxZCIsInN1YiI6IjY0ZjIzNjBiZTBjYTdmMDBhZTM5YmRiYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.UFkHevBZYjleei8IIez043l0kHUK8s2Eenxu-_4tt7c")
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
                    saveMovie(title,posterPath);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/savedMovies")
    public Set<String> getSavedMovies() {
        return savedMovieTitles;
    }
}
