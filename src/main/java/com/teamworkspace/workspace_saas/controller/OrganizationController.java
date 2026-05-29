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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Organizations", description = "Organization management endpoints")
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Operation(summary = "Create a new organization")
    @PostMapping
    public OrganizationResponse createOrganization(@Valid @RequestBody OrganizationRequest request) {
        return organizationService.createOrganization(request);
    }

    @Operation(summary = "Get all organizations")
    @GetMapping
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @Operation(summary = "Get organization by ID")
    @GetMapping("/{id}")
    public OrganizationResponse getOrganizationById(@PathVariable Long id) {
        return organizationService.getOrganizationById(id);
    }

    @Operation(summary = "Update organization")
    @PutMapping("/{id}")
    public OrganizationResponse updateOrganization(@PathVariable Long id, @Valid @RequestBody OrganizationRequest request) {
        return organizationService.updateOrganization(id, request);
    }

    @Operation(summary = "Delete organization")
    @DeleteMapping("/{id}")
    public String deleteOrganization(@PathVariable Long id) {
        return organizationService.deleteOrganization(id);
    }
}
