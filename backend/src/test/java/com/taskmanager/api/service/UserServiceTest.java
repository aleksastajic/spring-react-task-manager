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
        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = Mockito.mock(org.springframework.security.crypto.password.PasswordEncoder.class);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenAnswer(i -> "encoded-" + i.getArgument(0));
        UserServiceImpl userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);
        User user = new User();
        user.setUsername("testuser");
        User saved = userService.createUser(user, "pass");
        assertEquals("testuser", saved.getUsername());
    }

    @Test
    void updateUserChangesFields() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = Mockito.mock(org.springframework.security.crypto.password.PasswordEncoder.class);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenAnswer(i -> "encoded-" + i.getArgument(0));
        UserServiceImpl userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);

        User user = new User(); user.setId(1L); user.setUsername("u1");
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        com.taskmanager.api.dto.UserUpdateDto dto = new com.taskmanager.api.dto.UserUpdateDto();
        dto.setUsername("newu"); dto.setPassword("newpass");

        User updated = userService.updateUser(1L, dto);
        assertEquals("newu", updated.getUsername());
        assertEquals("encoded-newpass", updated.getPassword());
    }

    @Test
    void deleteUserRemoves() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = Mockito.mock(org.springframework.security.crypto.password.PasswordEncoder.class);
        UserServiceImpl userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);

        User user = new User(); user.setId(42L);
        Mockito.when(userRepository.findById(42L)).thenReturn(java.util.Optional.of(user));

        userService.deleteUser(42L);
        Mockito.verify(userRepository).delete(user);
    }
}
