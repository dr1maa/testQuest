package com.tq.testQuest.models;

import javax.persistence.*;

@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    private String posterPath;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "not_in_favorites")
    private boolean notInFavorites;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isNotInFavorites() {
        return notInFavorites;
    }

    public void setNotInFavorites(boolean notInFavorites) {
        this.notInFavorites = notInFavorites;
    }

    public Movie() {
    }

    public Movie(String title, String posterPath, boolean notInFavorites) {
        this.title = title;
        this.posterPath = posterPath;
        this.notInFavorites = notInFavorites;
    }
}
