package com.tq.testQuest.repositories;

import com.tq.testQuest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String name);

    User findByEmail(String email);

    Optional<User> findById(Long id);


}
