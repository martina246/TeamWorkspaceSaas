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
public class OrganizationResponse {
    private Long id;
    private String name;
    private String status;
    private LocalDateTime createdAt;
}
