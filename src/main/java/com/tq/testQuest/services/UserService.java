package com.tq.testQuest.services;

import com.tq.testQuest.models.User;

public interface UserService {
    User findById(User userId);

    User registerUser(User user);

    User getUserById(Long userId);

    User updateUserById(Long userId, User updatedUser);

    void deleteUser(Long userId);

    User updateUserByUsername(String username, User updatedUser);

    void deleteUserByUsername(String username);

    User getUserByUsername(String username);

}
