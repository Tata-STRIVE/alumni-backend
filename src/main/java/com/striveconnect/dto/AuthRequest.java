package com.striveconnect.dto;

/**
 * Data Transfer Object for the initial login request to generate an OTP.
 */
public class AuthRequest {
    private String mobileNumber;
    private String tenantId;

    // Getters and Setters
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}

