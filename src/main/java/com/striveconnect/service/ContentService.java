package com.striveconnect.service;

import com.striveconnect.dto.ContentPostCreateDto;
import com.striveconnect.dto.ContentPostDto;
import com.striveconnect.entity.AlumniBatch;
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

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
     * 🔹 Fetch all posts by type (Story / Event) for a given tenant.
     */
    public List<ContentPostDto> getPosts(String postType, String tenantId) {
        ContentPost.PostType type = ContentPost.PostType.valueOf(postType.toUpperCase());
        List<ContentPost> posts = contentPostRepository.findByTenantIdAndPostTypeAndIsActiveTrue(tenantId, type);

        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 🔹 Create new post (Admin only) — either a Story or Event.
     */
    public ContentPostDto createPost(ContentPostCreateDto dto) {
        User admin = getCurrentUser();
        String tenantId = admin.getTenantId();

        ContentPost post = new ContentPost();
        post.setTenantId(tenantId);
        post.setAuthor(admin);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        ContentPost.PostType type = ContentPost.PostType.valueOf(dto.getPostType().toUpperCase());
        post.setPostType(type);

        if (type == ContentPost.PostType.SUCCESS_STORY) {
            User alumnusUser = userRepository.findById(dto.getAlumnusUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumnus user not found."));
            post.setAlumnusUser(alumnusUser);
            post.setStudentPhotoUrl(dto.getStudentPhotoUrl());
        } else if (type == ContentPost.PostType.EVENT) {
            post.setEventDate(dto.getEventDate());
        }

        ContentPost saved = contentPostRepository.save(post);
        return convertToDto(saved);
    }

    /**
     * 🔹 Update an existing post (Admin only).
     */
    public ContentPostDto updatePost(Long postId, ContentPostCreateDto dto) {
        ContentPost post = contentPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update inactive post");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setPostType(ContentPost.PostType.valueOf(dto.getPostType().toUpperCase()));

        if (post.getPostType() == ContentPost.PostType.SUCCESS_STORY) {
            if (dto.getAlumnusUserId() != null) {
                User alumnusUser = userRepository.findById(dto.getAlumnusUserId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumnus user not found."));
                post.setAlumnusUser(alumnusUser);
            }
            post.setStudentPhotoUrl(dto.getStudentPhotoUrl());
            post.setEventDate(null);
        } else if (post.getPostType() == ContentPost.PostType.EVENT) {
            post.setEventDate(dto.getEventDate());
            post.setAlumnusUser(null);
            post.setStudentPhotoUrl(null);
        }

        ContentPost saved = contentPostRepository.save(post);
        return convertToDto(saved);
    }

    /**
     * 🔹 Soft delete a post (Admin only).
     */
    public void softDeletePost(Long postId) {
        ContentPost post = contentPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        post.setActive(false);
        contentPostRepository.save(post);
    }

    // ---------------------------------------------------------------------
    // 🔹 Helper Methods
    // ---------------------------------------------------------------------

    private User getCurrentUser() {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobile, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found."));
    }

    /**
     * 🔹 Convert ContentPost → DTO
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

            // Safely pick one alumniBatch (e.g., latest by batch.startDate)
            if (alumnus.getAlumniBatches() != null && !alumnus.getAlumniBatches().isEmpty()) {
                AlumniBatch latest = alumnus.getAlumniBatches().stream()
                    .filter(ab -> ab != null && ab.getBatch() != null)
                    .max((a, b) -> {
                        LocalDate aDate = a.getBatch().getStartDate();
                        LocalDate bDate = b.getBatch().getStartDate();
                        if (aDate == null && bDate == null) return 0;
                        if (aDate == null) return -1;
                        if (bDate == null) return 1;
                        return aDate.compareTo(bDate);
                    })
                    .orElse(null);

                if (latest != null && latest.getBatch() != null) {
                    Batch batch = latest.getBatch();
                    dto.setAlumnusBatchName(batch.getBatchName());
                    if (batch.getCenter() != null) {
                        dto.setAlumnusCenterName(batch.getCenter().getName());
                    }
                    // Optional: expose startDate or other batch fields if DTO has them
                    // dto.setAlumnusBatchStartDate(batch.getStartDate());
                }
            }
        } else if (post.getPostType() == ContentPost.PostType.EVENT) {
            dto.setEventDate(post.getEventDate());
        }

        return dto;
    }
}
