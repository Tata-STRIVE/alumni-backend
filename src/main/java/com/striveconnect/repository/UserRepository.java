package com.striveconnect.repository;

import com.striveconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByMobileNumberAndTenantId(String mobileNumber, String tenantId);
    
    List<User> findByTenantIdAndStatus(String tenantId, User.Status status);

    List<User> findAllByMobileNumberAndTenantId(String mobileNumber, String tenantId);

    /**
     * NEW: Counts users by tenant, role, and status.
     * Used for the admin dashboard stat card.
     */
    long countByTenantIdAndRoleAndStatus(String tenantId, User.Role role, User.Status status);
}

