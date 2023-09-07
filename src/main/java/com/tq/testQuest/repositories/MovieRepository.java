package com.tq.testQuest.repositories;

import com.tq.testQuest.models.Movie;
import com.tq.testQuest.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByTitle(String title);

    List<Movie> findByNotInFavorites(Long userId);
    @Query("SELECT m FROM Movie m WHERE m.id NOT IN (SELECT fm.movie.id FROM FavoriteMovie fm WHERE fm.user = :user)")
    Page<Movie> findNonFavoriteMovies(@Param("user") User user, Pageable pageable);

}