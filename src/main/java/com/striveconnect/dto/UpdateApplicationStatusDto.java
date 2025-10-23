package com.striveconnect.dto;

/**
 * Data Transfer Object for updating an application's status by an Admin.
 * Contains only the new status.
 */
public class UpdateApplicationStatusDto {
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
