package com.teamworkspace.workspace_saas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.dto.response.ActivityLogResponse;
import com.teamworkspace.workspace_saas.entity.ActivityLog;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.exception.ForbiddenException;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.ActivityLogRepository;

@Service
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;
    private final CurrentUserService currentUserService;

    public ActivityLogService(ActivityLogRepository activityLogRepository, CurrentUserService currentUserService) {
        this.activityLogRepository = activityLogRepository;
        this.currentUserService = currentUserService;
    }

    public void logAction(String action, String description) {
        
        User currentUser = currentUserService.getCurrentUser();

        ActivityLog activityLog = new ActivityLog();
        activityLog.setAction(action);
        activityLog.setDescription(description);
        activityLog.setCreatedAt(LocalDateTime.now());
        activityLog.setUser(currentUser);
        activityLog.setOrganization(currentUser.getOrganization());

        activityLogRepository.save(activityLog);

    }

    public List<ActivityLogResponse> getAllLogs() {

        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() == User.Role.SUPERADMIN) {
            List<ActivityLog> activityLogs = activityLogRepository.findAll();

            List<ActivityLogResponse> responses = new ArrayList<>();

            for (ActivityLog activityLog : activityLogs) {
                ActivityLogResponse response = new ActivityLogResponse(
                    activityLog.getId(),
                    activityLog.getAction(),
                    activityLog.getDescription(),
                    activityLog.getCreatedAt(),
                    activityLog.getOrganization().getId(),
                    activityLog.getOrganization().getName(),
                    activityLog.getUser().getId(),
                    activityLog.getUser().getEmail()
                );
                responses.add(response);
            }

            return responses;
        }

        List<ActivityLog> activityLogs = activityLogRepository.findByOrganizationId(currentUser.getOrganization().getId());

        List<ActivityLogResponse> responses = new ArrayList<>();

        for (ActivityLog activityLog : activityLogs) {
            ActivityLogResponse response = new ActivityLogResponse(
                activityLog.getId(),
                activityLog.getAction(),
                activityLog.getDescription(),
                activityLog.getCreatedAt(),
                activityLog.getOrganization().getId(),
                activityLog.getOrganization().getName(),
                activityLog.getUser().getId(),
                activityLog.getUser().getEmail()
            );
            responses.add(response);
        }

        return responses;

    }

    public ActivityLogResponse getLogById(Long id) {
        User currentUser = currentUserService.getCurrentUser();

        ActivityLog activityLog = activityLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Activity log not found"));

        if (currentUser.getRole() != User.Role.SUPERADMIN && !activityLog.getOrganization().getId().equals(currentUser.getOrganization().getId())) {
            throw new ForbiddenException("You are not the owner of this activity log");
        }

        return new ActivityLogResponse(
            activityLog.getId(),
            activityLog.getAction(),
            activityLog.getDescription(),
            activityLog.getCreatedAt(),
            activityLog.getOrganization().getId(),
            activityLog.getOrganization().getName(),
            activityLog.getUser().getId(),
            activityLog.getUser().getEmail()    
        );
    }

    
}
