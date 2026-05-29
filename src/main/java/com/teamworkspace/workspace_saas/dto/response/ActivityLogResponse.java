package com.teamworkspace.workspace_saas.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponse {
    private Long id;
    private String action;
    private String description;
    private LocalDateTime createdAt;
    private Long organziationId;
    private String organizationName;
    private Long userId;
    private String userEmail;
}
