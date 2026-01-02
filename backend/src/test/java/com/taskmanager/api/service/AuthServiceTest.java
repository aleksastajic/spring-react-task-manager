package com.taskmanager.api.service;

import com.taskmanager.api.dto.AuthRequest;
import com.taskmanager.api.dto.AuthResponse;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.security.JwtUtils;
import com.taskmanager.api.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class AuthServiceTest {
    @Test
    void loginReturnsToken() {
        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
        JwtUtils jwtUtils = Mockito.mock(JwtUtils.class);
        UserService userService = Mockito.mock(UserService.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testuser");
        Mockito.when(jwtUtils.generateToken("testuser")).thenReturn("mock-token");

        AuthServiceImpl authService = new AuthServiceImpl(userService, authenticationManager, jwtUtils);
        AuthRequest req = new AuthRequest();
        req.setUsernameOrEmail("testuser");
        req.setPassword("pass");
        AuthResponse resp = authService.login(req);
        assertEquals("mock-token", resp.getToken());
    }
}
