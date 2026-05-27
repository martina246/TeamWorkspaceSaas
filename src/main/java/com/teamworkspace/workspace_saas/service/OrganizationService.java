package com.teamworkspace.workspace_saas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.dto.request.OrganizationRequest;
import com.teamworkspace.workspace_saas.dto.response.OrganizationResponse;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.entity.SubscriptionPlan;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.OrganizationRepository;
import com.teamworkspace.workspace_saas.repository.SubscriptionPlanRepository;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    

    public OrganizationService(OrganizationRepository organizationRepository,
            SubscriptionPlanRepository subscriptionPlanRepository) {
        this.organizationRepository = organizationRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    public OrganizationResponse createOrganization(OrganizationRequest request) {
        Optional<SubscriptionPlan> subPlanOpt = subscriptionPlanRepository.findById(request.getSubscriptionPlanId());
        SubscriptionPlan subscriptionPlan = subPlanOpt.orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));

        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setStatus(request.getStatus());
        organization.setCreatedAt(LocalDateTime.now());
        organization.setSubscriptionPlan(subscriptionPlan);

        organizationRepository.save(organization);

        return new OrganizationResponse(organization.getId(), organization.getName(), organization.getStatus(), organization.getCreatedAt(), organization.getSubscriptionPlan().getId(), organization.getSubscriptionPlan().getName());
    }

    public List<OrganizationResponse> getAllOrganizations() {
        List<Organization> organizations = organizationRepository.findAll();

        List<OrganizationResponse> responses = new ArrayList<>();

        for (Organization organization : organizations) {
            OrganizationResponse response = new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getStatus(),
                organization.getCreatedAt(),
                organization.getSubscriptionPlan().getId(),
                organization.getSubscriptionPlan().getName()
            );
            responses.add(response);
        }

        return responses;
    }

    public OrganizationResponse getOrganizationById(Long id) {

        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        return new OrganizationResponse(organization.getId(), organization.getName(), organization.getStatus(), organization.getCreatedAt(), organization.getSubscriptionPlan().getId(), organization.getSubscriptionPlan().getName());
    }


    public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
        
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Optional<SubscriptionPlan> subPlanOpt = subscriptionPlanRepository.findById(request.getSubscriptionPlanId());
        SubscriptionPlan subscriptionPlan = subPlanOpt.orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));

        organization.setName(request.getName());
        organization.setStatus(request.getStatus());
        organization.setSubscriptionPlan(subscriptionPlan);

        organizationRepository.save(organization);

        return new OrganizationResponse(organization.getId(), organization.getName(), organization.getStatus(), organization.getCreatedAt(), organization.getSubscriptionPlan().getId(), organization.getSubscriptionPlan().getName());
    }


    public String deleteOrganization(Long id) {
        
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        organizationRepository.delete(organization);

        return "Organization deleted.";
    }

    


}
