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

@RestController
@RequestMapping("/api/subscription-plans")
public class SubscriptionPlanController {
    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @PostMapping
    public SubscriptionPlanResponse createSubscriptionPlan(@RequestBody SubscriptionPlanRequest request) {
        return subscriptionPlanService.createSubscriptionPlan(request);
    }

    
    @GetMapping
    public List<SubscriptionPlanResponse> getAllSubscriptionPlans() {
        return subscriptionPlanService.getAllSubscriptionPlans();
    }

    @GetMapping("/{id}")
    public SubscriptionPlanResponse getSubscriptionPlanById(@PathVariable Long id) {
        return subscriptionPlanService.getSubscriptionPlanById(id);
    }

    @PutMapping("/{id}")
    public SubscriptionPlanResponse updateSubscriptionPlan(@PathVariable Long id, @RequestBody SubscriptionPlanRequest request) {
        return subscriptionPlanService.updateSubscriptionPlan(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteSubscriptionPlan(@PathVariable Long id) {
        return subscriptionPlanService.deleteSubscriptionPlan(id);
    }


}
