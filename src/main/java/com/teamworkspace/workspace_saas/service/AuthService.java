package com.teamworkspace.workspace_saas.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.dto.request.LoginRequest;
import com.teamworkspace.workspace_saas.dto.request.RegisterRequest;
import com.teamworkspace.workspace_saas.dto.response.AuthResponse;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.entity.User.Role;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.OrganizationRepository;
import com.teamworkspace.workspace_saas.repository.UserRepository;
import com.teamworkspace.workspace_saas.security.JwtService;


@Service
public class AuthService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;



    public AuthService(UserRepository userRepository, OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        return new AuthResponse(
            null,
            "Email already exists",
            request.getEmail(),
            null
        );
    }

        String email = request.getEmail();

        String domain = email.substring(email.indexOf("@") + 1);

        Optional<Organization> organizationOpt = organizationRepository.findByDomain(domain);

        Organization organization = organizationOpt.orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

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
        user.setOrganization(organization);

        userRepository.save(user);

        return new AuthResponse(null, "Registration successful", user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse(null, "Invalid email or password", request.getEmail(), null);
        }

        String jwtToken = jwtService.generateToken(user);
        
        return new AuthResponse(jwtToken, "Login successful", user.getEmail(), user.getRole().name());


        
    }



}
