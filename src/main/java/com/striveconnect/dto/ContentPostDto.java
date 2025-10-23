package com.striveconnect.dto;

import java.time.LocalDateTime;

/**
 * Represents a "Content Post" (Success Story or Event) to be shown on the frontend.
 * This is the corrected version with all necessary fields.
 */
public class ContentPostDto {

    private Long postId;
    private String postType;
    private String title;
    private String content;
    private String authorName; // Admin who posted
    private LocalDateTime createdAt;
    
    // --- THE FIX IS HERE ---
    // Fields for Success Story
    private String studentPhotoUrl;
    private String alumnusName; // Name of the student in the story
    private String alumnusBatchName; // e.g., "Retail, Hyderabad (2022)"
    private String alumnusCenterName;

    // Fields for Event
    private LocalDateTime eventDate;

    // Getters and Setters
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getStudentPhotoUrl() { return studentPhotoUrl; }
    public void setStudentPhotoUrl(String studentPhotoUrl) { this.studentPhotoUrl = studentPhotoUrl; }
    public String getAlumnusName() { return alumnusName; }
    public void setAlumnusName(String alumnusName) { this.alumnusName = alumnusName; }
    public String getAlumnusBatchName() { return alumnusBatchName; }
    public void setAlumnusBatchName(String alumnusBatchName) { this.alumnusBatchName = alumnusBatchName; }
    public String getAlumnusCenterName() { return alumnusCenterName; }
    public void setAlumnusCenterName(String alumnusCenterName) { this.alumnusCenterName = alumnusCenterName; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
}

