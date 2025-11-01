package com.striveconnect.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

/**
 * Service interface for abstracting file storage operations. This contract
 * defines how files are stored and retrieved, decoupling the application logic
 * from the underlying storage technology (e.g., local disk, S3, GCS).
 */
public interface StorageService {

	/**
	 * Stores a file in the format: {tenantId}/{userId}/{filename}.
	 *
	 * @param file     The file content to store.
	 * @param tenantId The current tenant's ID.
	 * @param userId   The ID of the user uploading the file (used for
	 *                 segmentation).
	 * @return The unique path/URL of the stored file (e.g.,
	 *         "strive/user-1234/profile.jpg").
	 */
	String store(MultipartFile file, String tenantId, String userId);

	/**
	 * Loads the file path based on the unique stored file name.
	 *
	 * @param storedFilename The unique path/URL of the stored file.
	 * @return The file resource.
	 */
	Resource loadAsResource(String storedFilename);

	/* *//**
			 * Deletes a stored file.
			 *
			 * @param storedFilename The unique path/URL of the stored file.
			 *//*
				 * void delete(String storedFilename);
				 */

	/**
	 * Initializes the storage location (e.g., creating root directories).
	 * 
	 * @throws StorageException if initialization fails.
	 */
	void init();
}
