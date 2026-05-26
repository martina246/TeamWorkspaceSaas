package com.teamworkspace.workspace_saas.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    @NotBlank(message = "Task description is required")
    private String description;

    @NotBlank(message = "Task priority is required")
    private String priority;

    @NotBlank(message = "Task status is required")
    private String status;

    @NotNull(message = "Task due date is required")
    private LocalDate dueDate;

    @NotNull(message = "Assigned user is required")
    private Long assignedUserId;

    @NotNull(message = "Project is required")
    private Long projectId;
}
