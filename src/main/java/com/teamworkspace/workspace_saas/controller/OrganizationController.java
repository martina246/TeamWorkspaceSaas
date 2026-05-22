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

import com.teamworkspace.workspace_saas.dto.request.OrganizationRequest;
import com.teamworkspace.workspace_saas.dto.response.OrganizationResponse;
import com.teamworkspace.workspace_saas.service.OrganizationService;


@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public OrganizationResponse createOrganization(@RequestBody OrganizationRequest request) {
        return organizationService.createOrganization(request);
    }

    @GetMapping
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @GetMapping("/{id}")
    public OrganizationResponse getOrganizationById(@PathVariable Long id) {
        return organizationService.getOrganizationById(id);
    }

    @PutMapping("/{id}")
    public OrganizationResponse updateOrganization(@PathVariable Long id, @RequestBody OrganizationRequest request) {
        return organizationService.updateOrganization(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteOrganization(@PathVariable Long id) {
        return organizationService.deleteOrganization(id);
    }
}
