package com.taskmanager.api.service;

import com.taskmanager.api.entity.User;
import com.taskmanager.api.repository.UserRepository;
import com.taskmanager.api.repository.RoleRepository;
import com.taskmanager.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class UserServiceTest {
    @Test
    void createUserSavesUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        UserServiceImpl userService = new UserServiceImpl(userRepository, roleRepository);
        User user = new User();
        user.setUsername("testuser");
        User saved = userService.createUser(user, "pass");
        assertEquals("testuser", saved.getUsername());
    }
}
