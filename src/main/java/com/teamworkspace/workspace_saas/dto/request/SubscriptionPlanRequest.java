package com.teamworkspace.workspace_saas.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanRequest {
    private String name;
    private Double price;
    private Integer maxUsers;
    private Integer maxProjects;
}
