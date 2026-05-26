package com.teamworkspace.workspace_saas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanRequest {

    @NotBlank(message = "Subscription plan name is required")
    private String name;

    @Positive(message = "Price must be positive")
    @NotNull(message = "Subscription plan price is required")
    private Double price;

    @Positive(message = "Max number of users must be positive")
    @NotNull(message = "You must set max users")
    private Integer maxUsers;

    @NotNull(message = "You must set max projects")
    @Positive(message = "Max number of projects must be positive")
    private Integer maxProjects;
}
