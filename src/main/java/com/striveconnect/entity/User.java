package com.striveconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String userId; // UUID or assigned key

    @Column(name = "student_id", unique = true)
    private String studentId; // Permanent student identifier from source system

   

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String mobileNumber;

    private String email;
    private String profilePictureUrl;
    private String highestEducationQualification;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    // ✅ One-to-Many relationship to AlumniBatch (the Engagement-equivalent)
    @OneToMany(mappedBy = "alumnus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlumniBatch> alumniBatches = new ArrayList<>();

    // Optional employment tracking
    @Column(name = "current_company")
    private String currentCompany;

    @Column(name = "current_city")
    private String currentCity;

    // --- Audit Fields ---
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // --- Enums ---
    public enum Role {
        ALUMNUS,
        CENTER_ADMIN,
        SUPER_ADMIN,
        PLATFORM_ADMIN
    }

    public enum Status {
        PENDING_APPROVAL,
        ACTIVE,
        INACTIVE
    }

    // --- Lifecycle Hooks ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }



    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getHighestEducationQualification() { return highestEducationQualification; }
    public void setHighestEducationQualification(String highestEducationQualification) { this.highestEducationQualification = highestEducationQualification; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Center getCenter() { return center; }
    public void setCenter(Center center) { this.center = center; }

    public List<AlumniBatch> getAlumniBatches() { return alumniBatches; }
    public void setAlumniBatches(List<AlumniBatch> alumniBatches) { this.alumniBatches = alumniBatches; }

    public String getCurrentCompany() { return currentCompany; }
    public void setCurrentCompany(String currentCompany) { this.currentCompany = currentCompany; }

    public String getCurrentCity() { return currentCity; }
    public void setCurrentCity(String currentCity) { this.currentCity = currentCity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
