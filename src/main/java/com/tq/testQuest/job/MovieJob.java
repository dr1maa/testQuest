package com.tq.testQuest.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.repositories.MovieRepository;
import com.tq.testQuest.services.MovieService;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
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


    @Scheduled(fixedRate = 3 * 60 * 60 * 1000)
    void fetchMoviesAndSave() throws IOException {
        for (int page = 1; page <= 5; page++) {
            fetchAndSaveMoviesFromPage(page);
        }
    }


    private void fetchAndSaveMoviesFromPage(int page) throws IOException {
        Response response = discoverClient.fetchDiscoverData();


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

            if (results != null && results.isArray()) {
                for (JsonNode movieNode : results) {
                    String title = movieNode.get("title").asText();
                    String posterPath = movieNode.get("poster_path").asText();

                    Optional<Movie> existingMovieOptional = movieRepository.findByTitle(title);

                    if (existingMovieOptional.isPresent()) {
                        Movie existingMovie = existingMovieOptional.get();
                        existingMovie.setPosterPath(posterPath);
                        movieRepository.save(existingMovie);
                    } else {
                        Movie newMovie = new Movie();
                        newMovie.setTitle(title);
                        newMovie.setPosterPath(posterPath);
                        movieRepository.save(newMovie);
                    }
                }
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}