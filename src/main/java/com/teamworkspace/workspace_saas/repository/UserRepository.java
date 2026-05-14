package com.teamworkspace.workspace_saas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamworkspace.workspace_saas.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
}
