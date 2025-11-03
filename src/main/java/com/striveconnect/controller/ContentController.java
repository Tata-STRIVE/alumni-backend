package com.striveconnect.controller;

import com.striveconnect.dto.ContentPostCreateDto;
import com.striveconnect.dto.ContentPostDto;
import com.striveconnect.service.ContentService;
import com.striveconnect.util.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * Public: Gets all content posts of a specific type (e.g., SUCCESS_STORY, EVENT)
     * The tenant is determined from the user's token or a public header.
     * For simplicity, we assume TenantContext is set.
     */
    @GetMapping("/{type}/tenantId/{tenantId}")
    public ResponseEntity<List<ContentPostDto>> getPosts(@PathVariable String type ,@PathVariable String tenantId ) {
        // This assumes a tenant context. For a truly public page, you'd
        // get the tenant from the domain (strive.localhost)
    	System.out.print("tenantId===>"+ tenantId);
    	if(tenantId==null)
         tenantId = TenantContext.getCurrentTenant(); 
        List<ContentPostDto> posts = contentService.getPosts(type, tenantId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Admin: Creates a new content post (Story or Event).
     */
    @PostMapping
    public ResponseEntity<ContentPostDto> createPost(@RequestBody ContentPostCreateDto createDto) {
        ContentPostDto createdPost = contentService.createPost(createDto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }
    
    
    @PutMapping("/{postId}")
    public ResponseEntity<ContentPostDto> updatePost(
            @PathVariable Long postId,
            @RequestBody ContentPostCreateDto updateDto) {

        ContentPostDto updatedPost = contentService.updatePost(postId, updateDto);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        contentService.softDeletePost(postId);
        return ResponseEntity.noContent().build();
    }
    
}

