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
    private final OtpService authService; // Assumed for OTP logic
   // private final SmsService smsService;     // Assumed for sending SMS

    public UserService(UserRepository userRepository, BatchRepository batchRepository, OtpService authService
    		//, SmsService smsService
    		) {
        this.userRepository = userRepository;
        this.batchRepository = batchRepository;
        this.authService = authService;
     //   this.smsService = smsService;
    }
    
    
    
    public UserDto registerUser(RegistrationRequestDto registrationRequest) {
        String tenantId = registrationRequest.getTenantId().trim().toUpperCase();
        String mobileNumber = registrationRequest.getMobileNumber().trim();

        userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
            .ifPresent(u -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with this mobile number already exists for this organization.");
            });
        
        // Find the selected batch
        User newUser = new User();

        if (registrationRequest.getBatchId() != null) {
        Batch batch = batchRepository.findById(registrationRequest.getBatchId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Selected batch not found."));

        // Security check: Ensure the batch belongs to the same tenant
        if (!batch.getTenantId().equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid batch selection.");
        }
        newUser.setBatch(batch); // Link the user to the batch
        }
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setTenantId(tenantId);
        newUser.setFullName(registrationRequest.getFullName());
        newUser.setMobileNumber(mobileNumber);
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setRole(User.Role.ALUMNUS);
        newUser.setStatus(User.Status.PENDING_APPROVAL);
        newUser.setHighestEducationQualification(registrationRequest.getHighestEducationQualification());
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setProfilePictureUrl(registrationRequest.getProfilePictureUrl());


        User savedUser = userRepository.save(newUser);
        return convertToDto(savedUser);
    }
    

   

    public UserDto getMyProfile() {
        User currentUser = getCurrentUser();
        return convertToDto(currentUser);
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
        dto.setHighestEducationQualification(user.getHighestEducationQualification());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        
        // --- This is the fix ---
        // Ensure the new fields from the entity are copied to the DTO
		/*
		 * dto.setCurrentCity(user.getCurrentCity());
		 * dto.setCurrentCompany(user.getCurrentCompany());
		 */
        
        return dto;
    }

    /**
     * Admin: Gets a list of all active alumni for the current tenant.
     * Used to populate the dropdown for Success Story creation.
     */
	public List<UserDto> getAlumnusListForAdmin() {
		String tenantId = TenantContext.getCurrentTenant();
        
        // Find all users who are ALUMNUS and ACTIVE in the current tenant
        List<User> alumni = userRepository.findByTenantIdAndRoleAndStatus(
            tenantId, 
            User.Role.ALUMNUS, 
            User.Status.ACTIVE
        );
        
        return alumni.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
	}
	
	 /**
     * Updates the profile for the currently authenticated user.
     */
    public UserDto updateMyProfile(UpdateProfileDto profileDto) {
        User currentUser = getCurrentUser();

        currentUser.setEmail(profileDto.getEmail());
        currentUser.setCurrentCity(profileDto.getCurrentCity());
        currentUser.setCurrentCompany(profileDto.getCurrentCompany());
        currentUser.setHighestEducationQualification(profileDto.getHighestEducationQualification());
        
        // Handle profile picture URL update
        if (profileDto.getProfilePictureUrl() != null) {
            currentUser.setProfilePictureUrl(profileDto.getProfilePictureUrl());
        }

        User updatedUser = userRepository.save(currentUser);
        return convertToDto(updatedUser);
    }

    /**
     * NEW: Step 1 - Request Mobile Change
     * Generates an OTP and sends it to the *new* mobile number.
     */
    public void requestMobileChangeOtp(String newMobileNumber) {
        String tenantId = TenantContext.getCurrentTenant();
        
        // Check if the new number is already in use by another user in the same tenant
        userRepository.findByMobileNumberAndTenantId(newMobileNumber, tenantId)
            .ifPresent(existingUser -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This mobile number is already in use by another account.");
            });

        // Generate a new OTP for the *new* mobile number
        String otp = authService.generateOtp(newMobileNumber);
        
        // Send the OTP via SMS
        String message = "Your OTP to change your Strive Connect mobile number is " + otp;
  //      smsService.sendSms(newMobileNumber, message);
    }

    /**
     * NEW: Step 2 - Verify Mobile Change
     * Validates the OTP and updates the user's mobile number.
     */
    public void verifyMobileChange(String newMobileNumber, String otp) {
        String tenantId = TenantContext.getCurrentTenant();
        User currentUser = getCurrentUser(); // Get the currently logged-in user

        // Validate the OTP against the *new* mobile number
        boolean isValidOtp = authService.validateOtp(newMobileNumber,  otp);
        
        if (!isValidOtp) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP.");
        }
        // Final check for safety
        userRepository.findByMobileNumberAndTenantId(newMobileNumber, tenantId)
            .ifPresent(existingUser -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This mobile number is already in use by another account.");
            });
            
        // --- THIS IS THE FIX ---
        // The two missing lines to save the change to the database.
        currentUser.setMobileNumber(newMobileNumber);
        userRepository.save(currentUser);
	
    }

}

