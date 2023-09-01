package com.tq.testQuest.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;


    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    private String email;

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username should contain only alphanumeric characters")
    private String username;

    public User(Long id, String email, String username, String name) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    private String name;


    public String getEmail() {
        return email;
    }
}
