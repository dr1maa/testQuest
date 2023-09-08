package com.tq.testQuest.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tq.testQuest.repositories.MovieRepository;
import com.tq.testQuest.services.MovieService;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.Set;

public class MovieJob {
    @Autowired

    private final ObjectMapper objectMapper;
    private final Set<String> savedMovieTitles;
    private final MovieRepository movieRepository;
    private final DiscoverClient discoverClient;
    private final MovieService movieService;

    @Autowired
    public MovieJob(ObjectMapper objectMapper, Set<String> savedMovieTitles, MovieRepository movieRepository, DiscoverClient discoverClient, MovieService movieService) {

        this.objectMapper = objectMapper;
        this.savedMovieTitles = savedMovieTitles;
        this.movieRepository = movieRepository;
        this.discoverClient = discoverClient;
        this.movieService = movieService;
    }


    @Scheduled(fixedRate = 10800000)
    void fetchMoviesAndSave() throws IOException {
        for (int page = 1; page <= 5; page++) {
            fetchAndSaveMoviesFromPage(page);
        }
    }


    private void fetchAndSaveMoviesFromPage(int page) throws IOException {
        Response response = discoverClient.getResponse();


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
                    movieService.saveMovieToDatabase(title, posterPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
