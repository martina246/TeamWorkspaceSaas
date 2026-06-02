package com.teamworkspace.workspace_saas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.teamworkspace.workspace_saas.dto.response.ActivityLogResponse;
import com.teamworkspace.workspace_saas.entity.ActivityLog;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.exception.ForbiddenException;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.ActivityLogRepository;
import com.teamworkspace.workspace_saas.service.ActivityLogService;
import com.teamworkspace.workspace_saas.service.CurrentUserService;

@ExtendWith(MockitoExtension.class)
public class ActivityLogServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock ActivityLogRepository activityLogRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    @Test
    void logAction_shouldSaveActivityLog() {
        Organization organization = new Organization();
        organization.setId(1L);

        User currentUser = new User();
        currentUser.setId(10L);
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        activityLogService.logAction("UPDATE_TEST", "Test Description");

        verify(activityLogRepository).save(any(ActivityLog.class));
        
    }

    @Test
    void getAll_shouldReturnAllLogs_whenUserIsSuperadmin() {
        User currentUser = new User();
        currentUser.setRole(User.Role.SUPERADMIN);

        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Test org");

        User user = new User();
        user.setId(10L);
        user.setEmail("user@test.com");
        
        ActivityLog log1 = new ActivityLog();
        log1.setId(1L);
        log1.setAction("CREATE_PROJECT");
        log1.setDescription("Project created");
        log1.setCreatedAt(LocalDateTime.now());
        log1.setOrganization(organization);
        log1.setUser(user);

        ActivityLog log2 = new ActivityLog();
        log2.setId(2L);
        log2.setAction("UPDATE_TASK");
        log2.setDescription("Task updated");
        log2.setCreatedAt(LocalDateTime.now());
        log2.setOrganization(organization);
        log2.setUser(user);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(activityLogRepository.findAll()).thenReturn(List.of(log1, log2));

        List<ActivityLogResponse> responses = activityLogService.getAllLogs();

        assertEquals(2, responses.size());
        assertEquals("CREATE_PROJECT", responses.get(0).getAction());

        verify(activityLogRepository).findAll();
        verify(activityLogRepository, never()).findByOrganizationId(any());
    }

    @Test
    void getAll_shouldReturnOnlyOrganizationLogs_whenUserIsNotSuperadmin() {
        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Org");

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);

        User user = new User();
        user.setId(10L);
        user.setEmail("user@test.com");

        ActivityLog log1 = new ActivityLog();
        log1.setId(1L);
        log1.setAction("CREATE_PROJECT");
        log1.setDescription("Project created");
        log1.setCreatedAt(LocalDateTime.now());
        log1.setOrganization(organization);
        log1.setUser(user);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(activityLogRepository.findByOrganizationId(1L)).thenReturn(List.of(log1));

        List<ActivityLogResponse> responses = activityLogService.getAllLogs();

        assertEquals(1, responses.size());
        assertEquals("CREATE_PROJECT", responses.get(0).getAction());

        verify(activityLogRepository, never()).findAll();
        verify(activityLogRepository).findByOrganizationId(1L);
}

    @Test
    void getById_shouldReturnLog_whenUserBelongsToOrganization() {
        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Org");

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);

        User user = new User();
        user.setId(10L);
        user.setEmail("user@test.com");

        ActivityLog activityLog = new ActivityLog();
        activityLog.setId(1L);
        activityLog.setAction("CREATE_TASK");
        activityLog.setDescription("Task created");
        activityLog.setCreatedAt(LocalDateTime.now());
        activityLog.setOrganization(organization);
        activityLog.setUser(user);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(activityLogRepository.findById(1L)).thenReturn(Optional.of(activityLog));

        ActivityLogResponse response = activityLogService.getLogById(1L);

        assertEquals(1L, response.getId());
        assertEquals("CREATE_TASK", response.getAction());
        assertEquals("Task created", response.getDescription());
        assertEquals(1L, response.getOrganziationId());
        assertEquals("Test Org", response.getOrganizationName());
        assertEquals(10L, response.getUserId());
        assertEquals("user@test.com", response.getUserEmail());
}

    @Test
    void getById_shouldThrowException_whenLogDoesNotExist() {
        when(activityLogRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> activityLogService.getLogById(999L)
        );

        assertEquals("Activity log not found", exception.getMessage());
}

    @Test
    void getById_shouldThrowException_whenLogBelongsToAnotherOrganization() {
        Organization organization1 = new Organization();
        organization1.setId(1L);

        Organization organization2 = new Organization();
        organization2.setId(2L);

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization2);

        User user = new User();
        user.setId(10L);
        user.setEmail("user@test.com");

        ActivityLog activityLog = new ActivityLog();
        activityLog.setId(1L);
        activityLog.setAction("DELETE_TASK");
        activityLog.setDescription("Task deleted");
        activityLog.setCreatedAt(LocalDateTime.now());
        activityLog.setOrganization(organization1);
        activityLog.setUser(user);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(activityLogRepository.findById(1L)).thenReturn(Optional.of(activityLog));

        ForbiddenException exception = assertThrows(
            ForbiddenException.class,
            () -> activityLogService.getLogById(1L)
        );

        assertEquals("You are not the owner of this activity log", exception.getMessage());
    }
}
