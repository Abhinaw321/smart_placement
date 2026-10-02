package com.smartplacement.service.impl;

import com.smartplacement.exception.FileStorageException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

/**
 * Filesystem-based implementation of FileStorageService.
 *
 * Implements strict security best practices:
 * - Sanitizes filenames to defeat path traversal attacks ("../../").
 * - Enforces PDF MIME type and extension constraints.
 * - Stores files with isolated UUID prefixes outside web-accessible roots.
 */
@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageServiceImpl.class);
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    private final Path resumeStorageLocation;

    public LocalFileStorageServiceImpl(@Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.resumeStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize()
                .resolve("resumes");

        try {
            Files.createDirectories(this.resumeStorageLocation);
            log.info("Initialized secure resume storage directory at: {}", this.resumeStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not initialize local directory for resume storage", ex);
        }
    }

    @Override
    public String storeResume(MultipartFile file, Long studentId) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Cannot upload an empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new FileStorageException("File size exceeds maximum permitted limit of 5 MB");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Security Guard 1: Prevent Directory Traversal Attacks
        if (originalFilename.contains("..")) {
            throw new FileStorageException("Filename contains invalid directory traversal sequence: " + originalFilename);
        }

        // Security Guard 2: Enforce PDF Extension
        if (!originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new FileStorageException("Only PDF documents (.pdf) are permitted for resume uploads");
        }

        // Security Guard 3: Enforce PDF Content-Type
        String contentType = file.getContentType();
        if (contentType != null && !contentType.equalsIgnoreCase("application/pdf")
                && !contentType.equalsIgnoreCase("application/octet-stream")) {
            throw new FileStorageException("Invalid file format. Uploaded MIME type is not application/pdf");
        }

        // Generate sanitized unique filename: student_{id}_{shortUuid}.pdf
        String uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String uniqueFilename = "student_" + studentId + "_" + uniqueId + ".pdf";

        try {
            Path targetLocation = this.resumeStorageLocation.resolve(uniqueFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("Successfully stored resume for student ID {} as {}", studentId, uniqueFilename);
            return uniqueFilename;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + uniqueFilename + ". Please try again.", ex);
        }
    }

    @Override
    public Resource loadResumeAsResource(String filename) {
        try {
            String sanitizedFilename = StringUtils.cleanPath(filename);
            if (sanitizedFilename.contains("..")) {
                throw new FileStorageException("Invalid filename sequence: " + sanitizedFilename);
            }

            Path filePath = this.resumeStorageLocation.resolve(sanitizedFilename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Resume file not found or is unreadable: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File path is invalid: " + filename);
        }
    }

    @Override
    public void deleteResume(String filename) {
        if (!StringUtils.hasText(filename)) {
            return;
        }

        try {
            String sanitized = StringUtils.cleanPath(filename);
            Path filePath = this.resumeStorageLocation.resolve(sanitized).normalize();
            Files.deleteIfExists(filePath);
            log.info("Deleted resume file: {}", filename);
        } catch (IOException ex) {
            log.warn("Could not delete resume file: {}", filename, ex);
        }
    }
}
