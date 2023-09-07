package com.tq.testQuest.models;

import javax.persistence.*;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false, unique = true)
    private String title;
    private String posterPath;
    private boolean notInFavorites; // Добавляем поле notInFavorites

    public boolean isNotInFavorites() {
        return notInFavorites;
    }

    public void setNotInFavorites(boolean notInFavorites) {
        this.notInFavorites = notInFavorites;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Movie() {
        this.id = id;
    }


    public Movie(Long id, String title, String poster_path) {
        this.id = id;
        this.title = title;
        this.posterPath = poster_path;
    }


}
