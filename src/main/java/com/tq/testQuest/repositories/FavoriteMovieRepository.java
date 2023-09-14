package com.tq.testQuest.repositories;

import com.tq.testQuest.models.FavoriteMovie;
import com.tq.testQuest.models.Movie;
import com.tq.testQuest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FavoriteMovieRepository extends JpaRepository<FavoriteMovie, Long> {

    FavoriteMovie findByUserAndMovie(User user, Movie movie);

    List<FavoriteMovie> findByUser(User user);

    List<Movie> findFavoriteMoviesByUserId(Long id);
}
