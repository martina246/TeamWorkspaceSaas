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
        SubscriptionPlan subscriptionPlan = subPlanOpt.orElseThrow();

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
        Optional<Organization> organizationOpt = organizationRepository.findById(id);

        if (!organizationOpt.isPresent()) {
            return new OrganizationResponse();
        }

        Organization organization = organizationOpt.get();

        return new OrganizationResponse(organization.getId(), organization.getName(), organization.getStatus(), organization.getCreatedAt(), organization.getSubscriptionPlan().getId(), organization.getSubscriptionPlan().getName());
    }

    public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
        Optional<Organization> organizationOpt = organizationRepository.findById(id);
        Organization organization = organizationOpt.orElseThrow();

        Optional<SubscriptionPlan> subPlanOpt = subscriptionPlanRepository.findById(request.getSubscriptionPlanId());
        SubscriptionPlan subscriptionPlan = subPlanOpt.orElseThrow();

        organization.setName(request.getName());
        organization.setStatus(request.getStatus());
        organization.setSubscriptionPlan(subscriptionPlan);

        organizationRepository.save(organization);

        return new OrganizationResponse(organization.getId(), organization.getName(), organization.getStatus(), organization.getCreatedAt(), organization.getSubscriptionPlan().getId(), organization.getSubscriptionPlan().getName());
    }

    public String deleteOrganization(Long id) {
        Optional<Organization> organizationOpt = organizationRepository.findById(id);
        Organization organization = organizationOpt.orElseThrow();

        organizationRepository.delete(organization);

        return "Organization deleted.";
    }

    


}
