package com.tq.testQuest.models;

import javax.persistence.*;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false, unique = true)
    private String title;
    private String poster_path;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public Movie(Long id) {
        this.id = id;
    }


    public Movie(Long id, String title, String poster_path) {
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
    }
}
