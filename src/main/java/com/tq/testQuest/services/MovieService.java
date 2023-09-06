package com.tq.testQuest.services;

import com.tq.testQuest.models.Movie;
import com.tq.testQuest.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    @Autowired
    MovieRepository movieRepository;

    public Movie saveMovie(String title, String posterPath) {
        if (title.isEmpty() || posterPath.isEmpty()) {
            throw new RuntimeException("Недостаточно данных для сохранения фильма");
        }

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setPosterPath(posterPath);

        return movieRepository.save(movie);
    }

    public void deleteMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new RuntimeException("Фильм не найден");
        }

        movieRepository.deleteById(movieId);
    }
}
