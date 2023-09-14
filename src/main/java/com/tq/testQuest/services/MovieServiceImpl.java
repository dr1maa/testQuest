package com.tq.testQuest.services;

import com.tq.testQuest.models.FavoriteMovie;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.models.User;
import com.tq.testQuest.repositories.FavoriteMovieRepository;
import com.tq.testQuest.repositories.MovieRepository;
import com.tq.testQuest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private final MovieRepository movieRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final FavoriteMovieRepository favoriteMovieRepository;


    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, FavoriteMovieRepository favoriteMovieRepository, UserRepository userRepository) {
        this.movieRepository = movieRepository;
        this.favoriteMovieRepository = favoriteMovieRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Movie saveMovie(String title, String posterPath) {
        if (title.isEmpty() || posterPath.isEmpty()) {
            throw new IllegalArgumentException("Недостаточно данных для сохранения фильма");
        }

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setPosterPath(posterPath);

        return movieRepository.save(movie);
    }

    @Override
    public Page<Movie> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    @Override
    public Movie deleteMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new EntityNotFoundException("Фильм не найден");
        }

        movieRepository.deleteById(movieId);
        return null;
    }

    @Override
    public Movie findById(Long movieId) {
        return movieRepository.findById(movieId).orElse(null);
    }

    @Override
    public FavoriteMovie getFavoriteMovie(User user, Movie movie) {
        return favoriteMovieRepository.findByUserAndMovie(user, movie);
    }

    @Override
    public List<Movie> getNonFavoriteMovies(Long userId) {
        User user = userRepository.findById(userId);

        List<Movie> allMovies = movieRepository.findAll();

        List<Movie> nonFavoriteMovies = allMovies.stream()
                .filter(movie -> favoriteMovieRepository.findByUserAndMovie(user, movie) == null)
                .collect(Collectors.toList());

        return nonFavoriteMovies;
    }

    @Override
    public void addToFavorites(User user, Movie movie) {
        favoriteMovieRepository.save(new FavoriteMovie(user, movie));
    }

    @Override
    public void removeFromFavorites(User user, Movie movie) {
        FavoriteMovie existingFavorite = favoriteMovieRepository.findByUserAndMovie(user, movie);
        if (existingFavorite != null) {
            favoriteMovieRepository.delete(existingFavorite);
        }
    }
}