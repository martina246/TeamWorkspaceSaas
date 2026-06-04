package com.teamworkspace.workspace_saas;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.teamworkspace.workspace_saas.dto.request.LoginRequest;
import com.teamworkspace.workspace_saas.dto.request.RegisterRequest;
import com.teamworkspace.workspace_saas.dto.response.AuthResponse;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.OrganizationRepository;
import com.teamworkspace.workspace_saas.repository.UserRepository;
import com.teamworkspace.workspace_saas.security.JwtService;
import com.teamworkspace.workspace_saas.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateUser_whenEmailDoesNotExist() {
        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Rivian");
        organization.setDomain("rivian.com");

        RegisterRequest request = new RegisterRequest(
            "Zivko",
            "Zivkovic",
            "zivko@rivian.com",
            "password123"
        );

        when(userRepository.findByEmail("zivko@rivian.com"))
            .thenReturn(Optional.empty());

        when(organizationRepository.findByDomain("rivian.com"))
            .thenReturn(Optional.of(organization));

        when(passwordEncoder.encode("password123"))
            .thenReturn("hashedPassword");

        AuthResponse response = authService.register(request);

        assertEquals("Registration successful", response.getMessage());
        assertEquals("zivko@rivian.com", response.getEmail());
        assertEquals("USER", response.getRole());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldReturnError_whenEmailAlreadyExists() {
        User user = new User();
        user.setEmail("zivko@rivian.com");

        RegisterRequest request = new RegisterRequest(
            "Zivko",
            "Zivkovic",
            "zivko@rivian.com",
            "password123"
        );

        when(userRepository.findByEmail("zivko@rivian.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.register(request);

        assertEquals("Email already exists", response.getMessage());
        assertEquals("zivko@rivian.com", response.getEmail());
        assertNull(response.getRole());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenOrganizationDomainDoesNotExist() {
        RegisterRequest request = new RegisterRequest(
            "Zivko",
            "Zivkovic",
            "zivko@unknown.com",
            "password123"
        );

        when(userRepository.findByEmail("zivko@unknown.com"))
            .thenReturn(Optional.empty());

        when(organizationRepository.findByDomain("unknown.com"))
            .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> authService.register(request)
        );

        assertEquals("Organization not found", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {

        User user = new User();
        user.setEmail("zivko@gmail.com");
        user.setPassword("123456");
        user.setRole(User.Role.USER);

        LoginRequest request = new LoginRequest(
            "zivko@gmail.com",
            "123456"
        );

        when(userRepository.findByEmail("zivko@gmail.com")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "123456")).thenReturn(true);

        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("Login successful", response.getMessage());
        assertEquals("zivko@gmail.com", response.getEmail());
        assertEquals("USER", response.getRole());
        assertEquals("jwt-token", response.getToken());

        verify(jwtService).generateToken(user);

    }

    @Test
    void login_shouldReturnError_whenPasswordIsInvalid() {
        User user = new User();
        user.setEmail("zivko@gmail.com");
        user.setPassword("123456");
        user.setRole(User.Role.USER);

        LoginRequest request = new LoginRequest(
            "zivko@gmail.com",
            "111111"
        );

        when(userRepository.findByEmail("zivko@gmail.com")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("111111", "123456")).thenReturn(false);

        AuthResponse response = authService.login(request);

        assertEquals("Invalid email or password", response.getMessage());
        assertNull(response.getToken());
        assertNull(response.getRole());

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_shouldReturnError_whenUserDoesNotExist() {

        LoginRequest request = new LoginRequest(
            "zivko@gmail.com",
            "123456"
        );

        when(userRepository.findByEmail("zivko@gmail.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> authService.login(request));

        assertEquals("User not found", exception.getMessage());


        verify(jwtService, never()).generateToken(any());
    }
}
