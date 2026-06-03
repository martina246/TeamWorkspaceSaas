package com.teamworkspace.workspace_saas.integration;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.teamworkspace.workspace_saas.dto.request.LoginRequest;
import com.teamworkspace.workspace_saas.dto.request.RegisterRequest;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.repository.OrganizationRepository;
import com.teamworkspace.workspace_saas.repository.UserRepository;
import com.teamworkspace.workspace_saas.service.AuthService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Test
    void createProject_shouldCreateProject() throws Exception {

        Organization organization = new Organization();
        organization.setName("Test Organization");
        organization.setStatus("ACTIVE");
        organization.setCreatedAt(LocalDateTime.now());

        Organization savedOrganization =
                organizationRepository.save(organization);

        String email = "projectuser_" + System.currentTimeMillis() + "@gmail.com";

        authService.register(
                new RegisterRequest(
                        "Admin",
                        "User",
                        email,
                        "password123",
                        savedOrganization.getId()
                )
        );

        User user = userRepository.findByEmail(email).orElseThrow();
        user.setRole(User.Role.ADMIN);
        userRepository.save(user);

        String token = authService.login(
                new LoginRequest(email, "password123")
        ).getToken();

        String requestBody = """
        {
            "name":"Integration Project",
            "description":"Project created by integration test",
            "status":"ACTIVE",
            "organizationId": %d
        }
        """.formatted(savedOrganization.getId());

        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void getProjects_shouldReturnProjects() throws Exception {

        Organization organization = new Organization();
        organization.setName("Test Organization");
        organization.setStatus("ACTIVE");
        organization.setCreatedAt(LocalDateTime.now());

        Organization savedOrganization =
                organizationRepository.save(organization);

        String email = "projects_" + System.currentTimeMillis() + "@gmail.com";

        authService.register(
                new RegisterRequest(
                        "Admin",
                        "User",
                        email,
                        "password123",
                        savedOrganization.getId()
                )
        );

        User user = userRepository.findByEmail(email).orElseThrow();
        user.setRole(User.Role.ADMIN);
        userRepository.save(user);

        String token = authService.login(
                new LoginRequest(email, "password123")
        ).getToken();

        mockMvc.perform(get("/api/projects")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
