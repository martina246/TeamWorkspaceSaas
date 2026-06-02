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

import com.teamworkspace.workspace_saas.dto.request.CommentRequest;
import com.teamworkspace.workspace_saas.dto.response.CommentResponse;
import com.teamworkspace.workspace_saas.entity.Comment;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.entity.Project;
import com.teamworkspace.workspace_saas.entity.Task;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.exception.ForbiddenException;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.CommentRepository;
import com.teamworkspace.workspace_saas.repository.TaskRepository;
import com.teamworkspace.workspace_saas.repository.UserRepository;
import com.teamworkspace.workspace_saas.service.ActivityLogService;
import com.teamworkspace.workspace_saas.service.CommentService;
import com.teamworkspace.workspace_saas.service.CurrentUserService;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CommentService commentService;
    
    @Test
    void createComment_shouldCreateComment_whenRequestIsValid() {
        Task task = new Task();
        task.setId(1L);

        User author = new User();
        author.setId(10L);
        author.setEmail("author@test.com");
        
        CommentRequest request = new CommentRequest(
            "Test content",
            10L,
            1L
        );

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(10L)).thenReturn(Optional.of(author));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponse response = commentService.createComment(request);

        assertEquals("Test content", response.getContent());
        assertEquals(1L, response.getTaskId());
        assertEquals("author@test.com", response.getAuthorEmail());

        verify(commentRepository).save(any(Comment.class));
        verify(activityLogService).logAction(anyString(), anyString());
    }

    @Test
    void createComment_shouldThrowException_whenUserDoesNotExist() {
        CommentRequest request = new CommentRequest(
            "Test content",
            10L,
            1L
        );

        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(request));

        assertEquals("User not found", exception.getMessage());

        verify(commentRepository, never()).save(any(Comment.class));


    }

    @Test
    void createComment_shouldThrowException_whenTaskDoesNotExist() {
        User user = new User();
        user.setId(10L);

        CommentRequest request = new CommentRequest(
            "Test content",
            10L,
            1L
        );

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(request));

        assertEquals("Task not found", exception.getMessage());

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getById_shouldReturnComment_whenUserBelongsToOrganization() {
        Organization organization = new Organization();
        organization.setId(1L);

        Project project = new Project();
        project.setId(10L);
        project.setOrganization(organization);

        User user = new User();
        user.setId(100L);
        user.setEmail("user@test.com");
        user.setRole(User.Role.ADMIN);
        user.setOrganization(organization);

        Task task = new Task();
        task.setId(1000L);
        task.setProject(project);

        Comment comment = new Comment();
        comment.setId(10000L);
        comment.setContent("Test content");
        comment.setUser(user);
        comment.setTask(task);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(commentRepository.findById(10000L)).thenReturn(Optional.of(comment));

        CommentResponse response = commentService.getCommentById(10000L);

        assertEquals(10000L, response.getId());
        assertEquals("Test content", response.getContent());
        assertEquals("user@test.com", response.getAuthorEmail());
        assertEquals(1000L, response.getTaskId());
    }

    @Test
    void getById_shouldThrowException_whenCommentDoesNotExist() {

        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(999L));

        assertEquals("Comment not found", exception.getMessage());
    }

    @Test
    void getById_shouldThrowException_whenCommentBelongsToAnotherOrganization() {
        Organization organization1 = new Organization();
        organization1.setId(1L);

        Organization organization2 = new Organization();
        organization2.setId(2L);

        User currentUser = new User();
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization2);

        Project project1 = new Project();
        project1.setId(10L);
        project1.setOrganization(organization1);

        Task task1 = new Task();
        task1.setId(100L);
        task1.setProject(project1);

        Comment comment = new Comment();
        comment.setId(1000L);
        comment.setTask(task1);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentRepository.findById(1000L)).thenReturn(Optional.of(comment));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> commentService.getCommentById(1000L));

        assertEquals("You are not the owner of this comment", exception.getMessage());

    }

    @Test
    void update_shouldUpdateComment_whenUserBelongsToOrganization() {
        Organization organization1 = new Organization();
        organization1.setId(1L);

        User currentUser = new User();
        currentUser.setId(10000L);
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization1);
        currentUser.setEmail("user@test.com");

        Project project1 = new Project();
        project1.setId(10L);
        project1.setOrganization(organization1);

        Task task1 = new Task();
        task1.setId(100L);
        task1.setProject(project1);

        Comment comment = new Comment();
        comment.setId(1000L);
        comment.setContent("Test content");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(currentUser);
        comment.setTask(task1);

        CommentRequest request = new CommentRequest(
            "Updated content",
            10000L,
            100L
        );

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentRepository.findById(1000L)).thenReturn(Optional.of(comment));

        CommentResponse response = commentService.updateComment(1000L, request);

        assertEquals("Updated content", response.getContent());
        assertEquals("user@test.com", response.getAuthorEmail());
        assertEquals(100L, response.getTaskId());

        verify(commentRepository).save(comment);
        verify(activityLogService).logAction(anyString(), anyString());
    }

    @Test
    void update_shouldThrowException_whenCommentDoesNotExist() {
    
        CommentRequest request = new CommentRequest(
            "Updated content",
            10000L,
            100L
        );

        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(999L, request));

        assertEquals("Comment not found", exception.getMessage());

        verify(commentRepository, never()).save(any(Comment.class));
        verify(activityLogService, never()).logAction(anyString(), anyString());
    }

    @Test
    void update_shouldThrowException_whenCommentBelongsToAnotherOrganization() {
        Organization organization1 = new Organization();
        organization1.setId(1L);

        Organization organization2 = new Organization();
        organization2.setId(2L);

        User currentUser = new User();
        currentUser.setId(10000L);
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization2);

        Project project = new Project();
        project.setId(10L);
        project.setOrganization(organization1);

        Task task = new Task();
        task.setId(100L);
        task.setProject(project);

        Comment comment = new Comment();
        comment.setId(1000L);
        comment.setTask(task);

        CommentRequest request = new CommentRequest(
            "Updated content",
            10000L,
            100L
        );

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentRepository.findById(1000L)).thenReturn(Optional.of(comment));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> commentService.updateComment(1000L, request));

        assertEquals("You are not the owner of this comment", exception.getMessage());

        verify(commentRepository, never()).save(any(Comment.class));
        verify(activityLogService, never()).logAction(anyString(), anyString());
    }

    @Test
    void delete_shouldDeleteComment_whenUserBelongsToOrganization() {
        Organization organization = new Organization();
        organization.setId(1L);

        User currentUser = new User();
        currentUser.setId(10L);
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization);

        Project project = new Project();
        project.setOrganization(organization);

        Task task = new Task();
        task.setProject(project);

        Comment comment = new Comment();
        comment.setId(10000L);
        comment.setTask(task);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentRepository.findById(10000L)).thenReturn(Optional.of(comment));

        String result = commentService.deleteComment(10000L);

        assertEquals("Comment deleted.", result);

        verify(commentRepository).delete(comment);
        verify(activityLogService).logAction(anyString(), anyString());
    }

    @Test
    void delete_shouldThrowException_whenCommentDoesNotExist() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(999L));

        assertEquals("Comment not found", exception.getMessage());

        verify(commentRepository, never()).delete(any(Comment.class));
        verify(activityLogService, never()).logAction(anyString(), anyString());
    }


    @Test
    void delete_shouldThrowException_whenCommentBelongsToAnotherOrganization() {
        Organization organization1 = new Organization();
        organization1.setId(1L);

        Organization organization2 = new Organization();
        organization2.setId(2L);

        User currentUser = new User();
        currentUser.setId(10000L);
        currentUser.setRole(User.Role.ADMIN);
        currentUser.setOrganization(organization2);

        Project project = new Project();
        project.setId(10L);
        project.setOrganization(organization1);

        Task task = new Task();
        task.setId(100L);
        task.setProject(project);

        Comment comment = new Comment();
        comment.setId(1000L);
        comment.setTask(task);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentRepository.findById(1000L)).thenReturn(Optional.of(comment));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> commentService.deleteComment(1000L));

        assertEquals("You are not the owner of this comment", exception.getMessage());

        verify(commentRepository, never()).delete(any(Comment.class));
        verify(activityLogService, never()).logAction(anyString(), anyString());
    }

    @Test
    void getAll_shouldReturnAllComments_whenUserIsSuperadmin() {
        User currentUser = new User();
        currentUser.setRole(User.Role.SUPERADMIN);

        User assignedUser = new User();
        assignedUser.setId(1000L);
        assignedUser.setRole(User.Role.USER);
        assignedUser.setEmail("user@test.com");

        Project project = new Project();
        project.setId(10L);
        project.setName("Test Project");

        Task task = new Task();
        task.setId(1L);
        task.setProject(project);

        Comment comment1 = new Comment();
        comment1.setContent("Comment 1 content");
        comment1.setCreatedAt(LocalDateTime.now());
        comment1.setUser(assignedUser);
        comment1.setTask(task);

        Comment comment2 = new Comment();
        comment2.setContent("Comment 2 content");
        comment2.setCreatedAt(LocalDateTime.now());
        comment2.setUser(assignedUser);
        comment2.setTask(task);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentRepository.findAll()).thenReturn(List.of(comment1, comment2));

        List<CommentResponse> responses = commentService.getAllComments();

        assertEquals(2, responses.size());
        assertEquals("Comment 1 content", responses.get(0).getContent());
        assertEquals("user@test.com", responses.get(0).getAuthorEmail());

        verify(commentRepository).findAll();
        verify(commentRepository, never()).findByTaskProjectOrganizationId(any());
    }

    @Test
    void getAll_shouldReturnOnlyOrganizationComments_whenUserIsNotSuperadmin() {
        Organization organization = new Organization();
        organization.setId(1L);

        User currentUser = new User();
        currentUser.setOrganization(organization);
        currentUser.setRole(User.Role.ADMIN);

        User assignedUser = new User();
        assignedUser.setId(1000L);
        assignedUser.setRole(User.Role.USER);
        assignedUser.setOrganization(organization);
        assignedUser.setEmail("user@test.com");

        Project project = new Project();
        project.setId(10L);
        project.setName("Test Project");
        project.setOrganization(organization);

        Task task = new Task();
        task.setId(100L);
        task.setProject(project);

        Comment comment1 = new Comment();
        comment1.setContent("Comment 1 content");
        comment1.setCreatedAt(LocalDateTime.now());
        comment1.setUser(assignedUser);
        comment1.setTask(task);

        Comment comment2 = new Comment();
        comment2.setContent("Comment 2 content");
        comment2.setCreatedAt(LocalDateTime.now());
        comment2.setUser(assignedUser);
        comment2.setTask(task);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentRepository.findByTaskProjectOrganizationId(1L)).thenReturn(List.of(comment1, comment2));

        List<CommentResponse> responses = commentService.getAllComments();

        assertEquals(2, responses.size());
        assertEquals("Comment 1 content", responses.get(0).getContent());

        verify(commentRepository, never()).findAll();
        verify(commentRepository).findByTaskProjectOrganizationId(1L);
    }




}
