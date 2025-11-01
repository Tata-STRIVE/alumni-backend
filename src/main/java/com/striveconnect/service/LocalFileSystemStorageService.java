package com.striveconnect.service;

import com.striveconnect.exception.StorageException;
import com.striveconnect.util.TenantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileSystemStorageService implements StorageService {

    private final Path rootLocation;

    // Inject the configured root directory from application.properties
    public LocalFileSystemStorageService(@Value("${app.storage.local.root-location:/tmp/alumni-files}") String rootPath) {
        this.rootLocation = Paths.get(rootPath);
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location.", e);
        }
    }

    @Override
    public String store(MultipartFile file, String tenantId, String userId) {
        // Normalize filename and create a unique file name
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Create the segmented directory: {root}/tenantId/userId
        Path userDirectory = rootLocation.resolve(tenantId).resolve(userId);
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            if (!Files.exists(userDirectory)) {
                Files.createDirectories(userDirectory);
            }

            // Save the file
            Path destinationFile = userDirectory.resolve(uniqueFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Return the URL path for the frontend (relative to /api/files/download)
            String relativePath = Paths.get(tenantId, userId, uniqueFilename).toString().replace('\\', '/');
            return "/api/files/download/" + relativePath;

        } catch (IOException e) {
            throw new StorageException("Failed to store file: " + originalFilename, e);
        }
    }

    @Override
    public Resource loadAsResource(String relativePath) {
        try {
            // Resolve the full path: {root}/relativePath
            Path file = rootLocation.resolve(relativePath).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Could not read file: " + relativePath);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Could not read file: " + relativePath, e);
        }
    }
}
