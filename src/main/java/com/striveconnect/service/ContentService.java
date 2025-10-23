package com.striveconnect.service;

import com.striveconnect.dto.ContentPostCreateDto;
import com.striveconnect.dto.ContentPostDto;
import com.striveconnect.entity.Batch;
import com.striveconnect.entity.Center;
import com.striveconnect.entity.ContentPost;
import com.striveconnect.entity.User;
import com.striveconnect.repository.ContentPostRepository;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private final ContentPostRepository contentPostRepository;
    private final UserRepository userRepository;

    public ContentService(ContentPostRepository contentPostRepository, UserRepository userRepository) {
        this.contentPostRepository = contentPostRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets all posts of a specific type for the current tenant.
     */
    public List<ContentPostDto> getPosts(String postType, String tenantId) {
        ContentPost.PostType type = ContentPost.PostType.valueOf(postType.toUpperCase());
        List<ContentPost> posts = contentPostRepository.findByTenantIdAndPostType(tenantId, type);
        
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Admin: Creates a new content post (Story or Event).
     */
    public ContentPostDto createPost(ContentPostCreateDto createDto) {
        User adminUser = getCurrentUser();
        String tenantId = adminUser.getTenantId();
        
        ContentPost post = new ContentPost();
        post.setTenantId(tenantId);
        post.setAuthor(adminUser);
        post.setTitle(createDto.getTitle());
        post.setContent(createDto.getContent());

        ContentPost.PostType type = ContentPost.PostType.valueOf(createDto.getPostType().toUpperCase());
        post.setPostType(type);

        if (type == ContentPost.PostType.SUCCESS_STORY) {
            User alumnusUser = userRepository.findById(createDto.getAlumnusUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumnus user not found."));
            post.setAlumnusUser(alumnusUser);
            post.setStudentPhotoUrl(createDto.getStudentPhotoUrl());
        } else if (type == ContentPost.PostType.EVENT) {
            post.setEventDate(createDto.getEventDate());
        }

        ContentPost savedPost = contentPostRepository.save(post);
        return convertToDto(savedPost);
    }

    private User getCurrentUser() {
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authenticated user not found."));
    }

    /**
     * Converts a ContentPost entity to a rich DTO for the frontend.
     */
    private ContentPostDto convertToDto(ContentPost post) {
        ContentPostDto dto = new ContentPostDto();
        dto.setPostId(post.getPostId());
        dto.setPostType(post.getPostType().name());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        
        if (post.getAuthor() != null) {
            dto.setAuthorName(post.getAuthor().getFullName());
        }

        if (post.getPostType() == ContentPost.PostType.SUCCESS_STORY && post.getAlumnusUser() != null) {
            User alumnus = post.getAlumnusUser();
            dto.setAlumnusName(alumnus.getFullName());
            dto.setStudentPhotoUrl(post.getStudentPhotoUrl());
            if (alumnus.getBatch() != null) {
                Batch batch = alumnus.getBatch();
                Center center = batch.getCenter();
                dto.setAlumnusBatchName(batch.getBatchName());
                if (center != null) {
                    dto.setAlumnusCenterName(center.getName());
                }
            }
        } else if (post.getPostType() == ContentPost.PostType.EVENT) {
            dto.setEventDate(post.getEventDate());
        }
        
        return dto;
    }
}

