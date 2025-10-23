package com.striveconnect.service;

import com.striveconnect.dto.RegistrationRequestDto;
import com.striveconnect.dto.UpdateProfileDto;
import com.striveconnect.dto.UserDto;
import com.striveconnect.entity.Batch;
import com.striveconnect.entity.User;
import com.striveconnect.repository.BatchRepository;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BatchRepository batchRepository;

    public UserService(UserRepository userRepository, BatchRepository batchRepository) {
        this.userRepository = userRepository;
        this.batchRepository = batchRepository;
    }
    
    
    
    public UserDto registerUser(RegistrationRequestDto registrationRequest) {
        String tenantId = registrationRequest.getTenantId().trim().toUpperCase();
        String mobileNumber = registrationRequest.getMobileNumber().trim();

        userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
            .ifPresent(u -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with this mobile number already exists for this organization.");
            });
        
        // Find the selected batch
        Batch batch = batchRepository.findById(registrationRequest.getBatchId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Selected batch not found."));

        // Security check: Ensure the batch belongs to the same tenant
        if (!batch.getTenantId().equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid batch selection.");
        }

        User newUser = new User();
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setTenantId(tenantId);
        newUser.setFullName(registrationRequest.getFullName());
        newUser.setMobileNumber(mobileNumber);
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setRole(User.Role.ALUMNUS);
        newUser.setStatus(User.Status.PENDING_APPROVAL);
        newUser.setBatch(batch); // Link the user to the batch

        User savedUser = userRepository.save(newUser);
        return convertToDto(savedUser);
    }
    

   

    public UserDto getMyProfile() {
        User currentUser = getCurrentUser();
        return convertToDto(currentUser);
    }
    
    /**
     * Updates the profile for the currently authenticated user.
     */
    public UserDto updateMyProfile(UpdateProfileDto profileDto) {
        User currentUser = getCurrentUser();

        currentUser.setEmail(profileDto.getEmail());
        currentUser.setCurrentCity(profileDto.getCurrentCity());
        currentUser.setCurrentCompany(profileDto.getCurrentCompany());

        User updatedUser = userRepository.save(currentUser);
        // This will now correctly return the saved data
        return convertToDto(updatedUser);
    }

    public List<UserDto> getPendingApprovals() {
        String tenantId = TenantContext.getCurrentTenant();
        List<User> pendingUsers = userRepository.findByTenantIdAndStatus(tenantId, User.Status.PENDING_APPROVAL);
        return pendingUsers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void approveUser(String userId) {
        User adminUser = getCurrentUser();
        User userToApprove = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to approve not found."));

        if (!userToApprove.getTenantId().equals(adminUser.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to manage this user.");
        }

        userToApprove.setStatus(User.Status.ACTIVE);
        userRepository.save(userToApprove);
    }

    public long getAlumniCount() {
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.countByTenantIdAndRoleAndStatus(tenantId, User.Role.ALUMNUS, User.Status.ACTIVE);
    }

    private User getCurrentUser() {
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authenticated user not found."));
    }

    /**
     * Converts a User entity to a UserDto, now including the new fields.
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setStatus(user.getStatus().name());
        
        // --- This is the fix ---
        // Ensure the new fields from the entity are copied to the DTO
        dto.setCurrentCity(user.getCurrentCity());
        dto.setCurrentCompany(user.getCurrentCompany());
        
        return dto;
    }
}

