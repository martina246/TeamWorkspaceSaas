package com.teamworkspace.workspace_saas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.teamworkspace.workspace_saas.dto.request.SubscriptionPlanRequest;
import com.teamworkspace.workspace_saas.dto.response.SubscriptionPlanResponse;
import com.teamworkspace.workspace_saas.service.SubscriptionPlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Subscription plans", description = "Subscription plan management endpoints")
@RestController
@RequestMapping("/api/subscription-plans")
public class SubscriptionPlanController {
    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @Operation(summary = "Create a new subscription plan")
    @PostMapping
    public SubscriptionPlanResponse createSubscriptionPlan(@Valid @RequestBody SubscriptionPlanRequest request) {
        return subscriptionPlanService.createSubscriptionPlan(request);
    }

    
    @Operation(summary = "Get all subscription plans")
    @GetMapping
    public List<SubscriptionPlanResponse> getAllSubscriptionPlans() {
        return subscriptionPlanService.getAllSubscriptionPlans();
    }

    @Operation(summary = "Get subscription plan by ID")
    @GetMapping("/{id}")
    public SubscriptionPlanResponse getSubscriptionPlanById(@PathVariable Long id) {
        return subscriptionPlanService.getSubscriptionPlanById(id);
    }

    @Operation(summary = "Update subscription plan")
    @PutMapping("/{id}")
    public SubscriptionPlanResponse updateSubscriptionPlan(@PathVariable Long id, @Valid @RequestBody SubscriptionPlanRequest request) {
        return subscriptionPlanService.updateSubscriptionPlan(id, request);
    }

    @Operation(summary = "Delete subscription plan")
    @DeleteMapping("/{id}")
    public String deleteSubscriptionPlan(@PathVariable Long id) {
        return subscriptionPlanService.deleteSubscriptionPlan(id);
    }


}
