package com.striveconnect.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "centers")
public class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long centerId;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @OneToMany(mappedBy = "center")
    private Set<Batch> batches;

    @OneToMany(mappedBy = "center")
    private Set<User> users;

    
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getCenterId() {
        return centerId;
    }

    public void setCenterId(Long centerId) {
        this.centerId = centerId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Set<Batch> getBatches() {
        return batches;
    }

    public void setBatches(Set<Batch> batches) {
        this.batches = batches;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
