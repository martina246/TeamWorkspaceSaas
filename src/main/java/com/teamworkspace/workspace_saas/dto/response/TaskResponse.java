package com.teamworkspace.workspace_saas.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDate dueDate;
    private Long assignedUserId;
    private String assignedUserEmail;
    private Long projectId;
    private String projectName;
}
