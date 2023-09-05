package com.tq.testQuest.services;

import com.tq.testQuest.models.Movie;
import com.tq.testQuest.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    @Autowired
    MovieRepository movieRepository;

    public Movie saveMovie(Movie movie) {
        if (movie.getTitle().isEmpty())
            throw new RuntimeException("фильм уже сохранен");
        return movieRepository.save(movie);
    }

    public Movie deliteMovie(Movie movie) {
        if (movie.getTitle().isEmpty())
            throw new RuntimeException("фильм не найден");
        movieRepository.delete(movie);
        return null;
    }

}
