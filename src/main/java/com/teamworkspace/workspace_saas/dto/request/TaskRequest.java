package com.teamworkspace.workspace_saas.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDate dueDate;
    private Long assignedUserId;
    private Long projectId;
}
