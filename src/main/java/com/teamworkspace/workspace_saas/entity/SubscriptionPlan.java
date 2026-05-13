package com.teamworkspace.workspace_saas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long price;
    private Long maxUsers;
    private Long maxProjects;

    

    public SubscriptionPlan() {
    }

    public SubscriptionPlan(Long id, String name, Long price, Long maxUsers, Long maxProjects) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.maxUsers = maxUsers;
        this.maxProjects = maxProjects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Long maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Long getMaxProjects() {
        return maxProjects;
    }

    public void setMaxProjects(Long maxProjects) {
        this.maxProjects = maxProjects;
    }

    @Override
    public String toString() {
        return "SubscriptionPlan [id=" + id + ", name=" + name + ", price=" + price + ", maxUsers=" + maxUsers
                + ", maxProjects=" + maxProjects + "]";
    }


    
    
    
}
