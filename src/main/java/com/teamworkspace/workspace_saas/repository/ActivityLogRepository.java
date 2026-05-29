package com.teamworkspace.workspace_saas.repository;

import com.teamworkspace.workspace_saas.entity.ActivityLog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByOrganizationId(Long organizationId);
}
