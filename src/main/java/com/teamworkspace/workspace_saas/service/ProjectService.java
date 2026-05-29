package com.teamworkspace.workspace_saas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.dto.request.ProjectRequest;
import com.teamworkspace.workspace_saas.dto.response.ProjectResponse;
import com.teamworkspace.workspace_saas.entity.Organization;
import com.teamworkspace.workspace_saas.entity.Project;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.exception.ForbiddenException;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.OrganizationRepository;
import com.teamworkspace.workspace_saas.repository.ProjectRepository;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final ActivityLogService activityLogService;
    private final CurrentUserService currentUserService;


    public ProjectService(ProjectRepository projectRepository, OrganizationRepository organizationRepository,
            ActivityLogService activityLogService, CurrentUserService currentUserService) {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.activityLogService = activityLogService;
        this.currentUserService = currentUserService;
    }



    public ProjectResponse createProject(ProjectRequest request) {
        Optional<Organization> organizationOpt = organizationRepository.findById(request.getOrganizationId());
        Organization organization = organizationOpt.orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStatus(request.getStatus());
        project.setCreatedAt(LocalDateTime.now());

        project.setOrganization(organization);

        projectRepository.save(project);

        activityLogService.logAction("CREATE_PROJECT", "Project " + project.getName() + " created");

        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getStatus(), project.getCreatedAt(), project.getOrganization().getId(), project.getOrganization().getName());
    }


    
    public List<ProjectResponse> getAllProjects() {

        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() == User.Role.SUPERADMIN) {
            List<Project> projects = projectRepository.findAll();

            List<ProjectResponse> responses = new ArrayList<>();
        
            //mapping
            //frontend treba listu DTOjeva,
            //a ne listu entityja
            for (Project project : projects) {
                ProjectResponse response = new ProjectResponse(
                    project.getId(),
                    project.getName(),
                    project.getDescription(),
                    project.getStatus(),
                    project.getCreatedAt(),
                    project.getOrganization().getId(),
                    project.getOrganization().getName()
                );
                responses.add(response);
            }
            return responses;
        }

        List<Project> projects = projectRepository.findByOrganizationId(currentUser.getOrganization().getId());

        List<ProjectResponse> responses = new ArrayList<>();
        
            //mapping
            //frontend treba listu DTOjeva,
            //a ne listu entityja
            for (Project project : projects) {
                ProjectResponse response = new ProjectResponse(
                    project.getId(),
                    project.getName(),
                    project.getDescription(),
                    project.getStatus(),
                    project.getCreatedAt(),
                    project.getOrganization().getId(),
                    project.getOrganization().getName()
                );
                responses.add(response);
            }
            return responses;

        
    }

    public ProjectResponse getProjectById(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (currentUser.getRole() != User.Role.SUPERADMIN && !project.getOrganization().getId().equals(currentUser.getOrganization().getId())) {
            throw new ForbiddenException("You are not the owner of this project.");
        }

        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getStatus(), project.getCreatedAt(), project.getOrganization().getId(), project.getOrganization().getName());
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (currentUser.getRole() != User.Role.SUPERADMIN && !project.getOrganization().getId().equals(currentUser.getOrganization().getId())) {
            throw new ForbiddenException("You are not the owner of this project.");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStatus(request.getStatus());

        projectRepository.save(project);

        activityLogService.logAction("UPDATE_PROJECT", "Project " + project.getName() + " updated");

        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getStatus(), project.getCreatedAt(), project.getOrganization().getId(), project.getOrganization().getName());

    }

    public String deleteProject(Long id) {
        
        User currentUser = currentUserService.getCurrentUser();

        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (currentUser.getRole() != User.Role.SUPERADMIN && !project.getOrganization().getId().equals(currentUser.getOrganization().getId())) {
            throw new ForbiddenException("You are not the owner of this project.");
        }

        projectRepository.delete(project);

        activityLogService.logAction("DELETE_PROJECT", "Project " + project.getName() + " deleted");

        return "Project deleted.";
    }

}
