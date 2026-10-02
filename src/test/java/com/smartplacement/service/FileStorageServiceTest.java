package com.smartplacement.service;

import com.smartplacement.exception.FileStorageException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.service.impl.LocalFileStorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new LocalFileStorageServiceImpl(tempDir.toString());
    }

    @Test
    @DisplayName("Should successfully store a valid PDF file and return unique filename")
    void testStoreValidPdf() {
        byte[] pdfBytes = "%PDF-1.4 sample content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample_resume.pdf",
                "application/pdf",
                pdfBytes
        );

        String storedFilename = fileStorageService.storeResume(file, 42L);

        assertNotNull(storedFilename);
        assertTrue(storedFilename.startsWith("student_42_"));
        assertTrue(storedFilename.endsWith(".pdf"));

        Resource resource = fileStorageService.loadResumeAsResource(storedFilename);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    @DisplayName("Should throw FileStorageException when uploading an empty file")
    void testStoreEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        assertThrows(FileStorageException.class, () -> fileStorageService.storeResume(emptyFile, 1L));
    }

    @Test
    @DisplayName("Should reject non-PDF file extension")
    void testStoreNonPdfExtension() {
        MockMultipartFile exeFile = new MockMultipartFile(
                "file",
                "malicious.exe",
                "application/octet-stream",
                "malicious content".getBytes()
        );

        assertThrows(FileStorageException.class, () -> fileStorageService.storeResume(exeFile, 1L));
    }

    @Test
    @DisplayName("Should reject directory traversal attack sequence")
    void testPathTraversalAttack() {
        MockMultipartFile traversalFile = new MockMultipartFile(
                "file",
                "../../etc/passwd.pdf",
                "application/pdf",
                "%PDF-1.4 content".getBytes()
        );

        assertThrows(FileStorageException.class, () -> fileStorageService.storeResume(traversalFile, 1L));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when requesting non-existent file")
    void testLoadNonExistentFile() {
        assertThrows(ResourceNotFoundException.class, () ->
                fileStorageService.loadResumeAsResource("student_999_nonexistent.pdf")
        );
    }

    @Test
    @DisplayName("Should delete stored resume file cleanly")
    void testDeleteResume() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "to_delete.pdf",
                "application/pdf",
                "%PDF-1.4 delete me".getBytes()
        );

        String storedFilename = fileStorageService.storeResume(file, 50L);
        fileStorageService.deleteResume(storedFilename);

        assertThrows(ResourceNotFoundException.class, () ->
                fileStorageService.loadResumeAsResource(storedFilename)
        );
    }
}
