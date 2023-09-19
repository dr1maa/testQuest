package com.tq.testQuest.services;

import com.tq.testQuest.models.User;

public interface UserService {

    User findUserByEmail(String email);

    User registerUser(User user);

    User updateUserByUsername(String username, User updatedUser);

    void deleteUserByUsername(String username);

    User getUserByUsername(String username);

}
