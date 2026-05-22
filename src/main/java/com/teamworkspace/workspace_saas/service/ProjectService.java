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
import com.teamworkspace.workspace_saas.repository.OrganizationRepository;
import com.teamworkspace.workspace_saas.repository.ProjectRepository;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;

    

    public ProjectService(ProjectRepository projectRepository, OrganizationRepository organizationRepository) {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
    }



    public ProjectResponse createProject(ProjectRequest request) {
        Optional<Organization> organizationOpt = organizationRepository.findById(request.getOrganizationId());
        Organization organization = organizationOpt.orElseThrow();

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStatus(request.getStatus());
        project.setCreatedAt(LocalDateTime.now());

        project.setOrganization(organization);

        projectRepository.save(project);

        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getStatus(), project.getCreatedAt(), project.getOrganization().getId(), project.getOrganization().getName());
    }


    
    public List<ProjectResponse> getAllProjects() {
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

    public ProjectResponse getProjectById(Long id) {


        Optional<Project> projectOpt = projectRepository.findById(id);

        if (!projectOpt.isPresent()) {
            return new ProjectResponse();
        }

        Project project = projectOpt.get();

        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getStatus(), project.getCreatedAt(), project.getOrganization().getId(), project.getOrganization().getName());
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        Project project = projectOpt.orElseThrow();

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStatus(request.getStatus());

        projectRepository.save(project);

        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getStatus(), project.getCreatedAt(), project.getOrganization().getId(), project.getOrganization().getName());

    }

    public String deleteProject(Long id) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        Project project = projectOpt.orElseThrow();

        projectRepository.delete(project);

        return "Project deleted.";
    }

}
