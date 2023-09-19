package com.tq.testQuest.repositories;

import com.tq.testQuest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String name);

    User findByEmail(String email);

    User save(User user);

}
