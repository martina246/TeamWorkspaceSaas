package com.teamworkspace.workspace_saas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamworkspace.workspace_saas.dto.response.ActivityLogResponse;
import com.teamworkspace.workspace_saas.service.ActivityLogService;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public List<ActivityLogResponse> getAllLogs() {
        return activityLogService.getAllLogs();
    }

    @GetMapping("/{id}")
    public ActivityLogResponse getLogById(@PathVariable Long id) {
        return activityLogService.getLogById(id);
    }
    
}
