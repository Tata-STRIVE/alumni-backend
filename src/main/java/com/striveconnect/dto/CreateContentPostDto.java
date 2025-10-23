package com.striveconnect.dto;

/**
 * Data Transfer Object for creating a new content post.
 */
public class CreateContentPostDto {
    private String postType;
    private String title;
    private String content;

    // Getters and Setters
    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
