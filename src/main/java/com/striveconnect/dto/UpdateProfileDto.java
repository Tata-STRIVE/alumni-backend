package com.striveconnect.dto;

/**
 * DTO for carrying profile update data from the client.
 */
public class UpdateProfileDto {
    private String email;
    private String currentCompany;
    private String currentCity;
    private String profilePictureUrl;
    private String highestEducationQualification;
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
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
