package com.striveconnect.dto;

/**
 * Data Transfer Object for the Center entity.
 * Used to send center data (e.g., for dropdown lists) to the frontend.
 */
public class CenterDto {

    private Long centerId;
    private String name;
    private String city;

    // Getters and Setters
    public Long getCenterId() {
        return centerId;
    }

    public void setCenterId(Long centerId) {
        this.centerId = centerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
