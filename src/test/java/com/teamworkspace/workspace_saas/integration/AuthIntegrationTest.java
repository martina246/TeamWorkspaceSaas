package com.teamworkspace.workspace_saas.integration;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.teamworkspace.workspace_saas.dto.request.RegisterRequest;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.repository.OrganizationRepository;
import com.teamworkspace.workspace_saas.service.AuthService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import org.springframework.http.MediaType;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrganizationRepository organizationRepository;


    @Autowired
    private AuthService authService;

    private Organization organization;

    @BeforeEach
    void setUp() {

    organization = organizationRepository
            .findByDomain("rivian.com")
            .orElseGet(() -> {

                Organization org = new Organization();
                org.setName("Rivian");
                org.setStatus("ACTIVE");
                org.setDomain("rivian.com");
                org.setCreatedAt(LocalDateTime.now());

                return organizationRepository.save(org);
            });
        }

    @Test
    void register_shouldCreateUser() throws Exception {

        

        String email = "testuser_" + System.currentTimeMillis() + "@rivian.com";

        String requestBody = """
        {
            "firstName": "Test",
            "lastName": "User",
            "email": "%s",
            "password": "password123"
        }
        """.formatted(email);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void login_shouldReturnJwtToken() throws Exception {

        

        String email = "loginuser_" + System.currentTimeMillis() + "@rivian.com";

        RegisterRequest registerRequest = new RegisterRequest(
            "Test",
            "User",
            email,
            "password123"
        );

        authService.register(registerRequest);

        String requestBody = """
        {
            "email": "%s",
            "password": "password123"
        }
        """.formatted(email);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    
}
