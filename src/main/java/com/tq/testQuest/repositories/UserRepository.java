package com.tq.testQuest.repositories;

import com.tq.testQuest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String name);

    User findByEmail(String email);
}
