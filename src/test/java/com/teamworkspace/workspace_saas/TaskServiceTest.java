package com.teamworkspace.workspace_saas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamworkspace.workspace_saas.dto.request.TaskRequest;
import com.teamworkspace.workspace_saas.dto.response.TaskResponse;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.entity.Project;
import com.teamworkspace.workspace_saas.entity.Task;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.exception.ForbiddenException;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.ProjectRepository;
import com.teamworkspace.workspace_saas.repository.TaskRepository;
import com.teamworkspace.workspace_saas.repository.UserRepository;
import com.teamworkspace.workspace_saas.service.ActivityLogService;
import com.teamworkspace.workspace_saas.service.CurrentUserService;
import com.teamworkspace.workspace_saas.service.TaskService;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldCreateTask_whenRequestIsValid () {
        Project project = new Project();
        project.setId(100L);
        project.setName("Test Project");

        User user = new User();
        user.setId(10L);
        user.setEmail("user@test.com");

        TaskRequest request = new TaskRequest(
            "Test Task",
            "Test Description",
            "High",
            "In development",
            LocalDate.of(2026, 12, 3),
            10L,
            100L
        );

        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.createTask(request);

        assertEquals("Test Task", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals("High", response.getPriority());
        assertEquals("In development", response.getStatus());
        assertEquals(LocalDate.of(2026, 12, 3), response.getDueDate());
        assertEquals(10L, response.getAssignedUserId());
        assertEquals("user@test.com", response.getAssignedUserEmail());
        assertEquals(100L, response.getProjectId());
        assertEquals("Test Project", response.getProjectName());

        verify(taskRepository).save(any(Task.class));
        verify(activityLogService).logAction(anyString(), anyString());

    }

    @Test
    void createTask_shouldThrowException_whenProjectDoesNotExist () {
        TaskRequest request = new TaskRequest(
            "Test Task",
            "Test Description",
            "High",
            "In development",
            LocalDate.of(2026, 12, 3),
            10L,
            100L
        );

        when(projectRepository.findById(100L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));

        assertEquals("Project not found", exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_shouldThrowException_whenUserDoesNotExist () {
        Project project = new Project();
        project.setId(100L);

        TaskRequest request = new TaskRequest(
            "Test Task",
            "Test Description",
            "High",
            "In development",
            LocalDate.of(2026, 12, 3),
            10L,
            100L
        );

        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));

        assertEquals("User not found", exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getById_shouldReturnTask_whenUserBelongsToOrganization () {
        Project project = new Project();
        project.setId(10L);
        project.setName("Test project");

        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Org");

        project.setOrganization(organization);

        User user = new User();
        user.setId(1000L);
        user.setOrganization(organization); 
        user.setEmail("user@test.com");

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization); 
    
        Task task = new Task();
        task.setId(100L);
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setPriority("High");
        task.setStatus("In development");
        task.setDueDate(LocalDate.of(2026, 7, 1));
        task.setAssignedUser(user);
        task.setProject(project);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(100L);

        assertEquals(100L, response.getId());
        assertEquals("Task Title", response.getTitle());
        assertEquals("Task Description", response.getDescription());
        assertEquals("High", response.getPriority());
        assertEquals("In development", response.getStatus());
        assertEquals(LocalDate.of(2026, 7, 1), response.getDueDate());
        assertEquals(1000L, response.getAssignedUserId());
        assertEquals("user@test.com", response.getAssignedUserEmail());
        assertEquals(10L, response.getProjectId());
        assertEquals("Test project", response.getProjectName());
    }

    @Test
    void getById_shouldThrowException_whenTaskDoesNotExist () {

        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(999L));

        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    void getById_shouldThrowException_whenTaskBelongsToAnotherOrganization () {
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

        Task task = new Task();
        task.setId(1000L);
        task.setProject(project);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> taskService.getTaskById(1000L));

        assertEquals("You are not the owner of this task.", exception.getMessage());
    }

    @Test
    void update_shouldUpdateTask_whenUserBelongsToOrganization () {
        Organization organization = new Organization();
        organization.setId(1L);
        
        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);
        currentUser.setEmail("user@test.com");
        currentUser.setId(1000L);

        Project project = new Project();
        project.setId(100L);
        project.setName("Test Project");
        project.setOrganization(organization);

        Task task = new Task();
        task.setId(10L);
        task.setTitle("Updated Title");
        task.setDescription("Updated Description");
        task.setPriority("High");
        task.setStatus("In development");
        task.setDueDate(LocalDate.of(2026, 7, 1));
        task.setAssignedUser(currentUser);
        task.setProject(project);

        TaskRequest request = new TaskRequest(
            "Updated Task",
            "Updated Description",
            "Low",
            "Completed",
            LocalDate.of(2026, 6, 2),
            1000L,
            100L
        );

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1000L)).thenReturn(Optional.of(currentUser));
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        TaskResponse response = taskService.updateTask(10L, request);

        assertEquals("Updated Task", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
        assertEquals("Low", response.getPriority());
        assertEquals("Completed", response.getStatus());
        assertEquals(LocalDate.of(2026, 6, 2), response.getDueDate());
        assertEquals(1000L, response.getAssignedUserId());
        assertEquals("user@test.com", response.getAssignedUserEmail());
        assertEquals(100L, response.getProjectId());
        assertEquals("Test Project", response.getProjectName());

        verify(taskRepository).save(task);
        verify(activityLogService).logAction(anyString(), anyString());
    }

    @Test
    void update_shouldThrowException_whenTaskBelongsToAnotherOrganization () {
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


        Task task = new Task();
        task.setId(1000L);
        task.setProject(project);

        TaskRequest request = new TaskRequest(
            "Updated Task",
            "Updated Description",
            "Low",
            "Completed",
            LocalDate.of(2026, 6, 2),
            10L,
            100L
        );

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));
        

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> taskService.updateTask(1000L, request));

        assertEquals("You are not the owner of this task.", exception.getMessage());
        

        verify(taskRepository, never()).save(any(Task.class));
        verify(activityLogService, never()).logAction(anyString(), anyString());

    }

    @Test
    void delete_shouldDeleteTask_whenUserBelongsToOrganization () {
        Organization organization = new Organization();
        organization.setId(1L);

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);

        Project project = new Project();
        project.setId(10L);
        project.setOrganization(organization);

        Task task = new Task();
        task.setId(100L);
        task.setProject(project);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));

        String result = taskService.deleteTask(100L);

        assertEquals("Task deleted.", result);

        verify(taskRepository).delete(task);
        verify(activityLogService).logAction(anyString(), anyString());
    }

    @Test
    void delete_shouldThrowException_whenTaskBelongsToAnotherOrganization () {
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

        Task task = new Task();
        task.setId(1000L);
        task.setProject(project);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> taskService.deleteTask(1000L));

        assertEquals("You are not the owner of this task.", exception.getMessage());

        verify(taskRepository, never()).delete(any(Task.class));
        verify(activityLogService, never()).logAction(anyString(), anyString());
    }

    @Test
    void getAll_shouldReturnAllTasks_whenUserIsSuperadmin () {

        User currentUser = new User();
        currentUser.setRole(User.Role.SUPERADMIN);

        User assignedUser = new User();
        assignedUser.setId(1000L);
        assignedUser.setRole(User.Role.USER);
        assignedUser.setEmail("user@test.com");

        Project project = new Project();
        project.setId(10L);
        project.setName("Test Project");

        Task task1 = new Task();
        task1.setId(100L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setPriority("High");
        task1.setStatus("In development");
        task1.setDueDate(LocalDate.of(2026, 7, 1));
        task1.setAssignedUser(assignedUser);
        task1.setProject(project);

        Task task2 = new Task();
        task2.setId(101L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setPriority("Low");
        task2.setStatus("Done");
        task2.setDueDate(LocalDate.of(2026, 6, 1));
        task2.setAssignedUser(assignedUser);
        task2.setProject(project);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));

        List<TaskResponse> responses = taskService.getAllTasks();

        assertEquals(2, responses.size());
        assertEquals("Task 1", responses.get(0).getTitle());
        assertEquals("user@test.com", responses.get(0).getAssignedUserEmail());
        assertEquals("Test Project", responses.get(0).getProjectName());

        verify(taskRepository).findAll();
        verify(taskRepository, never()).findByProjectOrganizationId(any());
    }

    @Test
    void getAll_shouldReturnOnlyOrganizationTasks_whenUserIsNotSuperadmin () {
        Organization organization = new Organization();
        organization.setId(1L);


        User currentUser = new User();
        currentUser.setId(10L);
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);
        currentUser.setEmail("admin@test.com");

        User assignedUser = new User();
        assignedUser.setId(11L);
        currentUser.setRole(User.Role.USER);
        currentUser.setOrganization(organization);
        assignedUser.setEmail("assigned@test.com");

        Project project = new Project();
        project.setId(100L);
        project.setName("Test Project 1");
        project.setOrganization(organization);

        Task task1 = new Task();
        task1.setId(100L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setPriority("High");
        task1.setStatus("In development");
        task1.setDueDate(LocalDate.of(2026, 7, 1));
        task1.setAssignedUser(currentUser);
        task1.setProject(project);

        Task task2 = new Task();
        task2.setId(101L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setPriority("Low");
        task2.setStatus("Done");
        task2.setDueDate(LocalDate.of(2026, 6, 1));
        task2.setAssignedUser(assignedUser);
        task2.setProject(project);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findByProjectOrganizationId(1L)).thenReturn(List.of(task1, task2));

        List<TaskResponse> responses = taskService.getAllTasks();

        assertEquals(2, responses.size());
        assertEquals("Task 1", responses.get(0).getTitle());

        verify(taskRepository, never()).findAll();
        verify(taskRepository).findByProjectOrganizationId(1L);
    }
}
