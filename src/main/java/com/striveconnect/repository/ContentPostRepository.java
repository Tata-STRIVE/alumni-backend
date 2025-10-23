package com.striveconnect.repository;

import com.striveconnect.entity.ContentPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentPostRepository extends JpaRepository<ContentPost, Long> {

    // Find all posts for a specific tenant and of a specific type
    List<ContentPost> findByTenantIdAndPostType(String tenantId, ContentPost.PostType postType);
}
