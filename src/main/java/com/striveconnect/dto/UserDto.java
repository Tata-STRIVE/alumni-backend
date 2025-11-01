package com.striveconnect.dto;

/**
 * Data Transfer Object for viewing user profile information.
 * This is the corrected version that includes all profile fields.
 */
public class UserDto {
    private String userId;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String role;
    private String status;
    
    // --- THE FIX IS HERE ---
    // These fields were missing.
    private String currentCompany;
    private String currentCity;
    
    private String profilePictureUrl;
    private String highestEducationQualification;
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Getters and Setters for the new fields
    public String getCurrentCompany() { return currentCompany; }
    public void setCurrentCompany(String currentCompany) { this.currentCompany = currentCompany; }
    public String getCurrentCity() { return currentCity; }
    public void setCurrentCity(String currentCity) { this.currentCity = currentCity; }
	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}
	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}
	public String getHighestEducationQualification() {
		return highestEducationQualification;
	}
	public void setHighestEducationQualification(String highestEducationQualification) {
		this.highestEducationQualification = highestEducationQualification;
	}
    
    
    
}

