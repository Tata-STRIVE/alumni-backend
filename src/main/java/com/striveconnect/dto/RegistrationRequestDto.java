package com.striveconnect.dto;

/**
 * Data Transfer Object for a new alumnus registration request.
 * Now includes ID for batch selection.
 */
public class RegistrationRequestDto {
    private String fullName;
    private String mobileNumber;
    private String email;
    private String tenantId;
    private Long batchId; // Updated to use Batch ID
    private String profilePictureUrl;
    private String highestEducationQualification;

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public Long getBatchId() {
        return batchId;
    }
    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }
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

