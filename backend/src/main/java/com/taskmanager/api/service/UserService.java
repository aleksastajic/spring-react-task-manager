package com.taskmanager.api.service;

import com.taskmanager.api.dto.ProfileDto;
import com.taskmanager.api.entity.User;

import java.util.Optional;

public interface UserService {
    User createUser(User user, String rawPassword);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    User updateProfile(Long userId, ProfileDto profileDto);
}
