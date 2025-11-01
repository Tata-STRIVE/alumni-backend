package com.striveconnect.dto;

// Using Jakarta validation for automatic checks
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for Step 1 of the mobile change process: Requesting an OTP.
 */
public class MobileChangeRequestDto {

    @NotBlank(message = "New mobile number cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String newMobileNumber;

    // Getter and Setter
    public String getNewMobileNumber() {
        return newMobileNumber;
    }

    public void setNewMobileNumber(String newMobileNumber) {
        this.newMobileNumber = newMobileNumber;
    }
}
