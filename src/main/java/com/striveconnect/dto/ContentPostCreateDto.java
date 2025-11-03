	package com.striveconnect.dto;
	
	import java.time.LocalDateTime;
	
	/**
	 * DTO for an Admin to create a new Success Story or Event.
	 */
	public class ContentPostCreateDto {
	
	    private String postType; // "SUCCESS_STORY" or "EVENT"
	    private String title;
	    private String content;
	    
	    // For SUCCESS_STORY
	    private String studentPhotoUrl;
	    private String alumnusUserId; // The ID of the alumnus
	
	    // For EVENT
	    private LocalDateTime eventDate;
	
	    // Getters and Setters
	    public String getPostType() { return postType; }
	    public void setPostType(String postType) { this.postType = postType; }
	    public String getTitle() { return title; }
	    public void setTitle(String title) { this.title = title; }
	    public String getContent() { return content; }
	    public void setContent(String content) { this.content = content; }
	    public String getStudentPhotoUrl() { return studentPhotoUrl; }
	    public void setStudentPhotoUrl(String studentPhotoUrl) { this.studentPhotoUrl = studentPhotoUrl; }
	    public String getAlumnusUserId() { return alumnusUserId; }
	    public void setAlumnusUserId(String alumnusUserId) { this.alumnusUserId = alumnusUserId; }
	    public LocalDateTime getEventDate() { return eventDate; }
	    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
	}
