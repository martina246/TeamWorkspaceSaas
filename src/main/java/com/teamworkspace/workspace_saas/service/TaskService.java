package com.teamworkspace.workspace_saas.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.dto.request.TaskRequest;
import com.teamworkspace.workspace_saas.dto.response.TaskResponse;
import com.teamworkspace.workspace_saas.entity.Project;
import com.teamworkspace.workspace_saas.entity.Task;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.exception.ForbiddenException;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.ProjectRepository;
import com.teamworkspace.workspace_saas.repository.TaskRepository;
import com.teamworkspace.workspace_saas.repository.UserRepository;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserService currentUserService;
    private final ActivityLogService activityLogService;


    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
            ProjectRepository projectRepository, CurrentUserService currentUserService,
            ActivityLogService activityLogService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.currentUserService = currentUserService;
        this.activityLogService = activityLogService;
    }

    public TaskResponse createTask(TaskRequest request) {
        Optional<Project> projectOpt = projectRepository.findById(request.getProjectId());
        Project project = projectOpt.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Optional<User> userOpt = userRepository.findById(request.getAssignedUserId());
        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());

        task.setProject(project);
        task.setAssignedUser(user);

        Task savedTask = taskRepository.save(task);

        activityLogService.logAction("CREATE_TASK", "Task " + task.getTitle() + " created");

        return new TaskResponse(
            savedTask.getId(),
            savedTask.getTitle(),
            savedTask.getDescription(),
            savedTask.getPriority(),
            savedTask.getStatus(),
            savedTask.getDueDate(),
            savedTask.getAssignedUser().getId(),
            savedTask.getAssignedUser().getEmail(),
            savedTask.getProject().getId(),
            savedTask.getProject().getName()
        );
    }

    public List<TaskResponse> getAllTasks() {

        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() == User.Role.SUPERADMIN) {
            List<Task> tasks = taskRepository.findAll();

            List<TaskResponse> responses = new ArrayList<>();

            for (Task task : tasks) {
                TaskResponse response = new TaskResponse(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getPriority(),
                    task.getStatus(),
                    task.getDueDate(),
                    task.getAssignedUser().getId(),
                    task.getAssignedUser().getEmail(),
                    task.getProject().getId(),
                    task.getProject().getName()
                );
                responses.add(response);
            }

            return responses;
        }

        List<Task> tasks = taskRepository.findByProjectOrganizationId(currentUser.getOrganization().getId());

        List<TaskResponse> responses = new ArrayList<>();

        for (Task task : tasks) { 
            TaskResponse response = new TaskResponse(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getPriority(),
                    task.getStatus(),
                    task.getDueDate(),
                    task.getAssignedUser().getId(),
                    task.getAssignedUser().getEmail(),
                    task.getProject().getId(),
                    task.getProject().getName()
                );

                responses.add(response);
        }
        return responses;

    }

    public TaskResponse getTaskById(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (currentUser.getRole() != User.Role.SUPERADMIN && !task.getProject().getOrganization().getId().equals(currentUser.getOrganization().getId())) {
            throw new ForbiddenException("You are not the owner of this task.");
        }

        return new TaskResponse(
            task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getAssignedUser().getId(),
                task.getAssignedUser().getEmail(),
                task.getProject().getId(),
                task.getProject().getName()
        );
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (currentUser.getRole() != User.Role.SUPERADMIN && !task.getProject().getOrganization().getId().equals(currentUser.getOrganization().getId())) {
            throw new ForbiddenException("You are not the owner of this task.");
        }

        Optional<Project> projectOpt = projectRepository.findById(request.getProjectId());
        Project project = projectOpt.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User user = userRepository.findById(request.getAssignedUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());

        task.setProject(project);
        task.setAssignedUser(user);



        taskRepository.save(task);

        activityLogService.logAction("UPDATE_TASK", "Task " + task.getTitle() + " updated");

        return new TaskResponse(task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getAssignedUser().getId(),
                task.getAssignedUser().getEmail(),
                task.getProject().getId(),
                task.getProject().getName());

    }

    public String deleteTask(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (currentUser.getRole() != User.Role.SUPERADMIN && !task.getProject().getOrganization().getId().equals(currentUser.getOrganization().getId())) {
            throw new ForbiddenException("You are not the owner of this task.");
        }

        taskRepository.delete(task);

        activityLogService.logAction("DELETE_TASK", "Task " + task.getTitle() + " deleted");

        return "Task deleted.";
    }


}
