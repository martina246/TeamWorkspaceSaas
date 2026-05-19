package com.teamworkspace.workspace_saas.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.dto.request.LoginRequest;
import com.teamworkspace.workspace_saas.dto.request.RegisterRequest;
import com.teamworkspace.workspace_saas.dto.response.AuthResponse;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.entity.User.Role;
import com.teamworkspace.workspace_saas.repository.UserRepository;


@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new AuthResponse(null, "Email already exists", request.getEmail(), null);
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.USER);

        userRepository.save(user);

        return new AuthResponse(null, "Registration successful", user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {

        if (!userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new AuthResponse(null, "The user is not registered", request.getEmail(), null);
        }
        //znamo da Optional nije prazan (inace .orElseThrow())
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        User user = userOpt.get();


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse(null, "Invalid email or password", request.getEmail(), null);
        }
        
        return new AuthResponse(null, "Login successful", user.getEmail(), user.getRole().name());


        
    }



}
