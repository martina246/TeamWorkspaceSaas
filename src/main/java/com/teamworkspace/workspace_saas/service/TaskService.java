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
import com.teamworkspace.workspace_saas.repository.ProjectRepository;
import com.teamworkspace.workspace_saas.repository.TaskRepository;
import com.teamworkspace.workspace_saas.repository.UserRepository;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    

    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
            ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }


    public TaskResponse createTask(TaskRequest request) {
        Optional<Project> projectOpt = projectRepository.findById(request.getProjectId());
        Project project = projectOpt.orElseThrow();

        Optional<User> userOpt = userRepository.findById(request.getAssignedUserId());
        User user = userOpt.orElseThrow();

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());

        task.setProject(project);
        task.setAssignedUser(user);

        Task savedTask = taskRepository.save(task);

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

    public TaskResponse getTaskById(Long id) {


        Optional<Task> taskOpt = taskRepository.findById(id);

        if (!taskOpt.isPresent()) {
            return new TaskResponse();
        }

        Task task = taskOpt.get();

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
        Optional<Task> taskOpt = taskRepository.findById(id);
        Task task = taskOpt.orElseThrow();
        Optional<Project> projectOpt = projectRepository.findById(request.getProjectId());
        Project project = projectOpt.orElseThrow();

        Optional<User> userOpt = userRepository.findById(request.getAssignedUserId());
        User user = userOpt.orElseThrow();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());

        task.setProject(project);
        task.setAssignedUser(user);



        taskRepository.save(task);

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
        Optional<Task> taskOpt = taskRepository.findById(id);
        Task task = taskOpt.orElseThrow();

        taskRepository.delete(task);

        return "Task deleted.";
    }


}
