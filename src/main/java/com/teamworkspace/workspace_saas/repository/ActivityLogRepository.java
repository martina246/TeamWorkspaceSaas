package com.teamworkspace.workspace_saas.repository;

import com.teamworkspace.workspace_saas.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    
}
