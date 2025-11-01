package com.striveconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for Step 2 of the mobile change process: Verifying the OTP.
 */
public class MobileChangeVerifyDto {

    @NotBlank(message = "New mobile number cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String newMobileNumber;

    @NotBlank(message = "OTP cannot be blank")
    private String otp;

    // Getters and Setters
    public String getNewMobileNumber() {
        return newMobileNumber;
    }

    public void setNewMobileNumber(String newMobileNumber) {
        this.newMobileNumber = newMobileNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
