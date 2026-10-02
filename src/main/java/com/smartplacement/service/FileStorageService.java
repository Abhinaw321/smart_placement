package com.smartplacement.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service contract for secure file uploads and storage.
 */
public interface FileStorageService {

    /**
     * Stores an uploaded PDF resume on disk with UUID-based sanitization.
     *
     * @param file uploaded multipart file
     * @param studentId ID of the student owning the resume
     * @return stored unique filename
     */
    String storeResume(MultipartFile file, Long studentId);

    /**
     * Loads a stored resume from the secure filesystem directory as a Spring Resource.
     *
     * @param filename stored filename
     * @return Resource representing the file
     */
    Resource loadResumeAsResource(String filename);

    /**
     * Deletes a previously stored resume file from disk.
     *
     * @param filename stored filename to delete
     */
    void deleteResume(String filename);
}
