package com.teamworkspace.workspace_saas.dto.request;

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
public class OrganizationRequest {

    @NotBlank(message = "Organization name is required")
    private String name;

    @NotBlank(message = "Organization status is required")
    private String status;

    @NotNull
    private Long subscriptionPlanId;
}
