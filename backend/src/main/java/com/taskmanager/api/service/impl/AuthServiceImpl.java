package com.taskmanager.api.service.impl;

import com.taskmanager.api.dto.AuthRequest;
import com.taskmanager.api.dto.AuthResponse;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.service.AuthService;
import com.taskmanager.api.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        // JWT not implemented yet. This is a placeholder to be completed in security step.
        throw new UnsupportedOperationException("Login not implemented yet");
    }

    @Override
    public AuthResponse register(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setDisplayName(username);
        User created = userService.createUser(user, password);
        // return placeholder response; JWT issued in security step
        return new AuthResponse(null);
    }
}
