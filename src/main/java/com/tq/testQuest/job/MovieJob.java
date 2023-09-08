package com.tq.testQuest.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.repositories.MovieRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.Set;

public class MovieJob {
    @Autowired
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final Set<String> savedMovieTitles;
    private final MovieRepository movieRepository;

@Autowired
    public MovieJob(OkHttpClient client, ObjectMapper objectMapper, Set<String> savedMovieTitles, MovieRepository movieRepository) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.savedMovieTitles = savedMovieTitles;
        this.movieRepository = movieRepository;
    }


    @Scheduled(fixedRate = 10800000)
    void fetchMoviesAndSave() throws IOException {
        for (int page = 1; page <= 5; page++) {
            fetchAndSaveMoviesFromPage(page);
        }
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

    public void saveMovieToDatabase(String title, String posterPath) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setPosterPath(posterPath);
        movieRepository.save(movie);
    }
}
