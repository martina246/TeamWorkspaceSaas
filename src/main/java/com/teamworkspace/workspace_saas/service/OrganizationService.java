package com.teamworkspace.workspace_saas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.dto.request.OrganizationRequest;
import com.teamworkspace.workspace_saas.dto.response.OrganizationResponse;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.repository.OrganizationRepository;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public OrganizationResponse createOrganization(OrganizationRequest request) {
        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setStatus(request.getStatus());
        organization.setCreatedAt(LocalDateTime.now());

        organizationRepository.save(organization);

        return new OrganizationResponse(organization.getId(), organization.getName(), organization.getStatus(), organization.getCreatedAt());
    }

    public List<OrganizationResponse> getAllOrganizations() {
        List<Organization> organizations = organizationRepository.findAll();

        List<OrganizationResponse> responses = new ArrayList<>();

        for (Organization organization : organizations) {
            OrganizationResponse response = new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getStatus(),
                organization.getCreatedAt()
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

        return new OrganizationResponse(organization.getId(), organization.getName(), organization.getStatus(), organization.getCreatedAt());
    }

    public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
        Optional<Organization> organizationOpt = organizationRepository.findById(id);
        Organization organization = organizationOpt.orElseThrow();

        organization.setName(request.getName());
        organization.setStatus(request.getStatus());

        organizationRepository.save(organization);

        return new OrganizationResponse(organization.getId(), organization.getName(), organization.getStatus(), organization.getCreatedAt());
    }

    public String deleteOrganization(Long id) {
        Optional<Organization> organizationOpt = organizationRepository.findById(id);
        Organization organization = organizationOpt.orElseThrow();

        organizationRepository.delete(organization);

        return "Organization deleted.";
    }

    


}
