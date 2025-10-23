package com.striveconnect.dto;

/**
 * Data Transfer Object for displaying connection information.
 */
public class ConnectionDto {
    private Long connectionId;
    private String userId;
    private String userName;
    private String status;

    // Getters and Setters
    public Long getConnectionId() { return connectionId; }
    public void setConnectionId(Long connectionId) { this.connectionId = connectionId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
