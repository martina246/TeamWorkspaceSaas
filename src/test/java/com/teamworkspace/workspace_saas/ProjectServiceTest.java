package com.teamworkspace.workspace_saas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamworkspace.workspace_saas.dto.request.ProjectRequest;
import com.teamworkspace.workspace_saas.dto.response.ProjectResponse;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.entity.Project;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.exception.ForbiddenException;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.OrganizationRepository;
import com.teamworkspace.workspace_saas.repository.ProjectRepository;
import com.teamworkspace.workspace_saas.service.ActivityLogService;
import com.teamworkspace.workspace_saas.service.CurrentUserService;
import com.teamworkspace.workspace_saas.service.ProjectService;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_shouldCreateProject_whenRequestIsValid() {
        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Test organization");

        ProjectRequest request = new ProjectRequest(
            "Veb sistemi 2",
            "Java + Spring Boot aplication",
            "In development"
        );

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        ProjectResponse response = projectService.createProject(request);

        assertEquals("Veb sistemi 2", response.getName());
        assertEquals("Java + Spring Boot aplication", response.getDescription());
        assertEquals("In development", response.getStatus());
        assertEquals(1L, response.getOrganizationId());
        assertEquals("Test organization", response.getOrganizationName());

        verify(projectRepository).save(any(Project.class));
        verify(activityLogService).logAction(anyString(), anyString());
    }

    @Test
    void getById_shouldReturnProject_whenUserBelongsToOrganization () {
        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Org");

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);

        Project project = new Project();
        project.setId(100L);
        project.setName("Project A");
        project.setDescription("Description");
        project.setStatus("ACTIVE");
        project.setCreatedAt(LocalDateTime.now());
        project.setOrganization(organization);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.getProjectById(100L);

        assertEquals(100L, response.getId());
        assertEquals("Project A", response.getName());
        assertEquals("Description", response.getDescription());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(1L, response.getOrganizationId());
    }

    @Test
    void getById_shouldThrowException_whenProjectDoesNotExist() {
        

        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(999L));

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void getById_shouldThrowException_whenProjectBelongsToAnotherOrganization() {
    
        Organization organization1 = new Organization();
        organization1.setId(1L);

        Organization organization2 = new Organization();
        organization2.setId(2L);

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization2);

        Project project = new Project();
        project.setId(100L);
        project.setOrganization(organization1);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> projectService.getProjectById(100L));

        assertEquals("You are not the owner of this project.", exception.getMessage());

    }

    @Test
    void update_shouldUpdateProject_whenUserBelongsToOrganization() {

        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Org");

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);

        Project project = new Project();
        project.setId(100L);
        project.setName("Old name");
        project.setDescription("Old description");
        project.setStatus("ACTIVE");
        project.setCreatedAt(LocalDateTime.now());
        project.setOrganization(organization);

        ProjectRequest request = new ProjectRequest(
            "New name",
            "New description",
            "COMPLETED"
        );

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(projectRepository.findById(100L))
                .thenReturn(Optional.of(project));

        ProjectResponse response =
                projectService.updateProject(100L, request);

        assertEquals("New name", response.getName());
        assertEquals("New description", response.getDescription());
        assertEquals("COMPLETED", response.getStatus());

        verify(projectRepository).save(project);
        verify(activityLogService)
                .logAction(anyString(), anyString());
    }

    @Test
    void update_shouldThrowException_whenProjectBelongsToAnotherOrganization() {
        Organization organization1 = new Organization();
        organization1.setId(1L);

        Organization organization2 = new Organization();
        organization2.setId(2L);

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization2);

        Project project = new Project();
        project.setId(100L);
        project.setOrganization(organization1);

        ProjectRequest request = new ProjectRequest(
            "Updated project",
            "Updated description",
            "Completed"
        );

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> projectService.updateProject(100L, request));

        assertEquals("You are not the owner of this project.", exception.getMessage());

        verify(projectRepository, never()).save(any(Project.class));
        verify(activityLogService, never()).logAction(anyString(), anyString());
    }

    @Test
    void delete_shouldDeleteProject_whenUserBelongsToOrganization() {

        Organization organization = new Organization();
        organization.setId(1L);

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);

        Project project = new Project();
        project.setId(100L);
        project.setOrganization(organization);

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(projectRepository.findById(100L))
                .thenReturn(Optional.of(project));

        String result =
                projectService.deleteProject(100L);

        assertEquals("Project deleted.", result);

        verify(projectRepository).delete(project);
        verify(activityLogService)
                .logAction(anyString(), anyString());
    }

    @Test
    void delete_shouldThrowException_whenProjectBelongsToAnotherOrganization() {
        Organization organization1 = new Organization();
        organization1.setId(1L);

        Organization organization2 = new Organization();
        organization2.setId(2L);

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization2);

        Project project = new Project();
        project.setId(100L);
        project.setOrganization(organization1);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> projectService.deleteProject(100L));

        assertEquals("You are not the owner of this project.", exception.getMessage());

        verify(projectRepository, never()).delete(any(Project.class));
        verify(activityLogService, never()).logAction(anyString(), anyString());

    }
    
    @Test
    void getAll_shouldReturnAllProjects_whenUserIsSuperadmin() {

        User currentUser = new User();
        currentUser.setRole(User.Role.SUPERADMIN);

        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Org 1");

        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");
        project1.setDescription("Description 1");
        project1.setStatus("ACTIVE");
        project1.setCreatedAt(LocalDateTime.now());
        project1.setOrganization(organization);

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");
        project2.setDescription("Description 2");
        project2.setStatus("COMPLETED");
        project2.setCreatedAt(LocalDateTime.now());
        project2.setOrganization(organization);

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(projectRepository.findAll())
                .thenReturn(List.of(project1, project2));

        List<ProjectResponse> responses =
                projectService.getAllProjects();

        assertEquals(2, responses.size());
        assertEquals("Project 1", responses.get(0).getName());

        verify(projectRepository).findAll();

        verify(projectRepository, never())
                .findByOrganizationId(any());
    }

    @Test
    void getAll_shouldReturnOnlyOrganizationProjects_whenUserIsNotSuperadmin() {
        Organization organization1 = new Organization();
        organization1.setId(1L);
        organization1.setName("Org 1");

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization1);

        Project project1 = new Project();
        project1.setId(10L);
        project1.setName("Project 1");
        project1.setDescription("Description 1");
        project1.setStatus("ACTIVE");
        project1.setCreatedAt(LocalDateTime.now());
        project1.setOrganization(organization1);

        Project project2 = new Project();
        project2.setId(11L);
        project2.setName("Project 2");
        project2.setDescription("Description 2");
        project2.setStatus("ACTIVE");
        project2.setCreatedAt(LocalDateTime.now());
        project2.setOrganization(organization1);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.findByOrganizationId(1L)).thenReturn(List.of(project1, project2));

        List<ProjectResponse> responses = projectService.getAllProjects();

        assertEquals(2, responses.size());
        assertEquals("Project 1", responses.get(0).getName());
        assertEquals(1L, responses.get(0).getOrganizationId());

        verify(projectRepository, never()).findAll();
        verify(projectRepository).findByOrganizationId(1L);
    }
}
