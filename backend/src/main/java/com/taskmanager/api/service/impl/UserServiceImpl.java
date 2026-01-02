package com.taskmanager.api.service.impl;

import com.taskmanager.api.dto.ProfileDto;
import com.taskmanager.api.entity.Role;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.repository.RoleRepository;
import com.taskmanager.api.repository.UserRepository;
import com.taskmanager.api.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public User createUser(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        // assign ROLE_USER by default if present
        roleRepository.findByName("ROLE_USER").ifPresent(role -> user.setRoles(new HashSet<>() {{ add(role); }}));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findAll().stream().filter(u -> username.equals(u.getUsername())).findFirst();
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, ProfileDto profileDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setDisplayName(profileDto.getDisplayName());
        user.setEmail(profileDto.getEmail());
        return userRepository.save(user);
    }
}
