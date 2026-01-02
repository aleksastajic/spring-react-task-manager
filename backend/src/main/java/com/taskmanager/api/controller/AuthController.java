package com.taskmanager.api.controller;

import com.taskmanager.api.dto.AuthRequest;
import com.taskmanager.api.dto.AuthResponse;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.service.AuthService;
import com.taskmanager.api.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller exposing authentication endpoints: login and register.
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse resp = authService.login(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.debug("Register request for username={}", request.getUsername());
            AuthResponse resp = authService.register(request.getUsername(), request.getEmail(), request.getPassword());
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            logger.error("Register failed: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }
}
