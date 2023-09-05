package com.tq.testQuest.services;

import com.tq.testQuest.models.User;
import com.tq.testQuest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("User with this email already exists!");
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("User with this username already exists");
        }
        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUserById(Long userId, User updatedUser) {
        User existingUser = getUserById(userId);
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setName(updatedUser.getName());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {
        User existingUser = getUserById(userId);
        userRepository.delete(existingUser);
    }

    public User updateUserByUsername(String username, User updatedUser) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setName(updatedUser.getName());

        return userRepository.save(existingUser);
    }

    public void deleteUserByUsername(String username) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        userRepository.delete(existingUser);
    }


    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
