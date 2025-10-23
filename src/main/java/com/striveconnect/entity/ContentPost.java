package com.striveconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * UPDATED Entity for the `content_posts` table.
 * Now includes fields for Success Stories and Events.
 */
@Entity
@Table(name = "content_posts")
public class ContentPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType postType;

    @Column(nullable = false)
    private String title;
    
    @Lob
    private String content;
    
    // --- Fields for SUCCESS_STORY ---
    private String studentPhotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumnus_user_id")
    private User alumnusUser; // The alumnus this story is about

    // --- Fields for EVENT ---
    private LocalDateTime eventDate;
    
    // --- Admin who created this post ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum PostType {
        SUCCESS_STORY,
        EVENT,
        AWARENESS
    }

    // Getters and Setters
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public PostType getPostType() { return postType; }
    public void setPostType(PostType postType) { this.postType = postType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStudentPhotoUrl() { return studentPhotoUrl; }
    public void setStudentPhotoUrl(String studentPhotoUrl) { this.studentPhotoUrl = studentPhotoUrl; }
    public User getAlumnusUser() { return alumnusUser; }
    public void setAlumnusUser(User alumnusUser) { this.alumnusUser = alumnusUser; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

	