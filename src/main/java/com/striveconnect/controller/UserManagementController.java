package com.striveconnect.controller;

import com.striveconnect.dto.MobileChangeRequestDto;
import com.striveconnect.dto.MobileChangeVerifyDto;
import com.striveconnect.dto.UpdateProfileDto;
import com.striveconnect.dto.UserDto;
import com.striveconnect.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Alumnus: Gets their own profile information.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile() {
        UserDto userProfile = userService.getMyProfile();
        return ResponseEntity.ok(userProfile);
    }
    
    // NEW: Endpoint for an alumnus to update their own profile
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMyProfile(@RequestBody UpdateProfileDto profileDto) {
        UserDto updatedUser = userService.updateMyProfile(profileDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Admin: Gets a list of all users pending approval for their tenant.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<UserDto>> getPendingApprovals() {
        List<UserDto> pendingUsers = userService.getPendingApprovals();
        return ResponseEntity.ok(pendingUsers);
    }

    /**
     * Admin: Approves a user's registration.
     */
    @PostMapping("/{userId}/approve")
    public ResponseEntity<?> approveUser(@PathVariable String userId) {
        userService.approveUser(userId);
        return ResponseEntity.ok(Map.of("message", "User approved successfully."));
    }

    /**
     * NEW: Admin: Gets the total count of active alumni for their tenant.
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getAlumniCount() {
        long count = userService.getAlumniCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    
    @GetMapping("/alumnus/list")
    public ResponseEntity<List<UserDto>> getAlumnusUsers() {
        List<UserDto> alumnusList = userService.getAlumnusListForAdmin(); 
        return ResponseEntity.ok(alumnusList);
    }

    /**
     * NEW: Step 1 for Mobile Change
     * Alumnus requests an OTP for a *new* mobile number.
     */
    @PostMapping("/me/change-mobile/request")
    public ResponseEntity<?> requestMobileChangeOtp(@Valid @RequestBody MobileChangeRequestDto requestDto) {
        userService.requestMobileChangeOtp(requestDto.getNewMobileNumber());
        return ResponseEntity.ok(Map.of("message", "OTP sent to the new mobile number."));
    }

    /**
     * NEW: Step 2 for Mobile Change
     * Alumnus verifies the OTP and commits the change.
     */
    @PostMapping("/me/change-mobile/verify")
    public ResponseEntity<?> verifyMobileChange(@Valid @RequestBody MobileChangeVerifyDto verifyDto) {
        userService.verifyMobileChange(verifyDto.getNewMobileNumber(), verifyDto.getOtp());
        return ResponseEntity.ok(Map.of("message", "Mobile number updated successfully. Please log in again."));
    }

    
    
}

