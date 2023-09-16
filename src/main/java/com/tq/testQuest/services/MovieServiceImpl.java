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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private final MovieRepository movieRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private final FavoriteMovieRepository favoriteMovieRepository;


    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, FavoriteMovieRepository favoriteMovieRepository, UserRepository userRepository, UserService userService) {
        this.movieRepository = movieRepository;
        this.favoriteMovieRepository = favoriteMovieRepository;
        this.userService = userService;
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
    public List<FavoriteMovie> getFavoriteMovies(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        List<FavoriteMovie> favoriteMovies = favoriteMovieRepository.findAllFavoriteMovies(user);
        return favoriteMovies;
    }


    public List<Movie> getNonFavoriteMovies(Authentication authentication) {

        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        List<Movie> allMovies = movieRepository.findAll();

        List<Movie> nonFavoriteMovies = allMovies.stream()
                .filter(movie -> favoriteMovieRepository.findAllFavoriteMovies(user) == null)
                .collect(Collectors.toList());
        return nonFavoriteMovies;
    }

    @Override
    public void addToFavorites(User user, Movie movie) {
        favoriteMovieRepository.save(new FavoriteMovie(user, movie));
    }

    @Override
    public void removeFromFavorites(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        List<FavoriteMovie> existingFavorite = favoriteMovieRepository.findAllFavoriteMovies(user);

        if (existingFavorite != null) {
            for (FavoriteMovie favoriteMovie : existingFavorite) {
                favoriteMovieRepository.delete(favoriteMovie);
            }
        }
    }

}