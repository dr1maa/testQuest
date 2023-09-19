package com.tq.testQuest.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.repositories.MovieRepository;
import com.tq.testQuest.services.MovieService;
import lombok.extern.log4j.Log4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
public class MovieJob {

    public static final String RESULTS = "results";
    public static final String TITLE = "title";
    public static final String POSTER_PATH = "poster_path";
    private final ObjectMapper objectMapper;
    private final DiscoverClient discoverClient;
    private final MovieService movieService;

    @Autowired
    public MovieJob(ObjectMapper objectMapper, DiscoverClient discoverClient, MovieService movieService) {
        this.objectMapper = objectMapper;
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
        Response response = discoverClient.fetchDiscoverData(page);

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

            JsonNode results = jsonNode.get(RESULTS);

            if (results != null && results.isArray()) {
                for (JsonNode movieNode : results) {
                    String title = movieNode.get(TITLE).asText();
                    String posterPath = movieNode.get(POSTER_PATH).asText();

                    Optional<Movie> existingMovieOptional = movieService.findByTitle(title);

                    if (existingMovieOptional.isPresent()) {
                        Movie existingMovie = existingMovieOptional.get();
                        existingMovie.setPosterPath(posterPath);
                        movieService.saveMovie(existingMovie);
                    } else {
                        Movie newMovie = new Movie();
                        newMovie.setTitle(title);
                        newMovie.setPosterPath(posterPath);
                        movieService.saveMovie(newMovie);
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