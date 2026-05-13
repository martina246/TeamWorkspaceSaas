package com.teamworkspace.workspace_saas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String action;
    private String description;
    private LocalDateTime createdAt;

    

    public ActivityLog() {
    }

    public ActivityLog(Long id, String action, String description, LocalDateTime createdAt) {
        this.id = id;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ActivityLog [id=" + id + ", action=" + action + ", description=" + description + ", createdAt="
                + createdAt + "]";
    }

    
}
