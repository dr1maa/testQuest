package com.tq.testQuest.services;

import com.tq.testQuest.models.User;
import com.tq.testQuest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tq.testQuest.models.Enum.Role;

import java.util.HashSet;
import java.util.Set;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Пользователь с этим адресом электронной почты уже существует!");
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Пользователь с этим именем пользователя уже существует!");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        user.setRoles(roles);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User updateUserById(Long userId, User updatedUser) {
        User existingUser = getUserById(userId);
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setName(updatedUser.getName());
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User existingUser = getUserById(userId);
        userRepository.delete(existingUser);
    }

    @Override
    public User updateUserByUsername(String username, User updatedUser) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setName(updatedUser.getName());

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUserByUsername(String username) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        userRepository.delete(existingUser);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(User userId) {
        return userRepository.findById(userId.getId()).orElse(null);
    }
}
