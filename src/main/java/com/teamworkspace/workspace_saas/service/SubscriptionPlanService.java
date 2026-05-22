package com.teamworkspace.workspace_saas.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.dto.request.SubscriptionPlanRequest;
import com.teamworkspace.workspace_saas.dto.response.SubscriptionPlanResponse;
import com.teamworkspace.workspace_saas.entity.SubscriptionPlan;
import com.teamworkspace.workspace_saas.repository.SubscriptionPlanRepository;

@Service
public class SubscriptionPlanService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanService(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    public SubscriptionPlanResponse createSubscriptionPlan(SubscriptionPlanRequest request) {

        SubscriptionPlan subPlan = new SubscriptionPlan();
        subPlan.setName(request.getName());
        subPlan.setPrice(request.getPrice());
        subPlan.setMaxUsers(request.getMaxUsers());
        subPlan.setMaxProjects(request.getMaxProjects());

        subscriptionPlanRepository.save(subPlan);

        return new SubscriptionPlanResponse(subPlan.getId(), subPlan.getName(), subPlan.getPrice(), subPlan.getMaxUsers(), subPlan.getMaxProjects());

    }

    public List<SubscriptionPlanResponse> getAllSubscriptionPlans() {
        List<SubscriptionPlan> subPlans = subscriptionPlanRepository.findAll();

        List<SubscriptionPlanResponse> responses = new ArrayList<>();

        for (SubscriptionPlan subPlan : subPlans) {
            SubscriptionPlanResponse response = new SubscriptionPlanResponse(
                subPlan.getId(),
                subPlan.getName(),
                subPlan.getPrice(),
                subPlan.getMaxUsers(),
                subPlan.getMaxProjects()
            );

            responses.add(response);
        }

        return responses;
    }

    public SubscriptionPlanResponse getSubscriptionPlanById(Long id) {
        Optional<SubscriptionPlan> subPlanOpt = subscriptionPlanRepository.findById(id);

        if (!subPlanOpt.isPresent()) {
            return new SubscriptionPlanResponse();
        }

        SubscriptionPlan subPlan = subPlanOpt.get();

        return new SubscriptionPlanResponse(subPlan.getId(), subPlan.getName(), subPlan.getPrice(), subPlan.getMaxUsers(), subPlan.getMaxProjects());
    }

    public SubscriptionPlanResponse updateSubscriptionPlan(Long id, SubscriptionPlanRequest request) {
        Optional<SubscriptionPlan> subPlanOpt = subscriptionPlanRepository.findById(id);
        SubscriptionPlan subPlan = subPlanOpt.orElseThrow();

        subPlan.setName(request.getName());
        subPlan.setPrice(request.getPrice());
        subPlan.setMaxUsers(request.getMaxUsers());
        subPlan.setMaxProjects(request.getMaxProjects());

        subscriptionPlanRepository.save(subPlan);

        return new SubscriptionPlanResponse(subPlan.getId(), subPlan.getName(), subPlan.getPrice(), subPlan.getMaxUsers(), subPlan.getMaxProjects());
    }

    public String deleteSubscriptionPlan(Long id) {
        Optional<SubscriptionPlan> subPlanOpt = subscriptionPlanRepository.findById(id);
        SubscriptionPlan subPlan = subPlanOpt.orElseThrow();

        subscriptionPlanRepository.delete(subPlan);

        return "Subscription Plan deleted.";
    }
}
