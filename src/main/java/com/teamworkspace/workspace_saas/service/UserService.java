package com.teamworkspace.workspace_saas.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.dto.response.UserResponse;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public UserService(UserRepository userRepository, CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    public List<UserResponse> getAllUsers() {

        User currentUser = currentUserService.getCurrentUser();

        List<User> users;

        if (currentUser.getRole() == User.Role.SUPERADMIN) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findByOrganizationId(
                    currentUser.getOrganization().getId()
            );
        }

        List<UserResponse> responses = new ArrayList<>();

        for (User user : users) {
            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getOrganization().getName()
            );

            responses.add(response);
        }

        return responses;
    }

    public UserResponse getCurrentUser() {
        
        User currentUser = currentUserService.getCurrentUser();

        return new UserResponse(
            currentUser.getId(),
            currentUser.getFirstName(),
            currentUser.getLastName(),
            currentUser.getEmail(),
            currentUser.getRole().name(),
            currentUser.getOrganization().getName()
        );
    }
}
