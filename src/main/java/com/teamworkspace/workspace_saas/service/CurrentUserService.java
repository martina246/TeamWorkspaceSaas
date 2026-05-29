package com.teamworkspace.workspace_saas.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.entity.User;

@Service
public class CurrentUserService {
    
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
