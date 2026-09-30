package com.foodrescue.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Saves uploaded files (NID/document images, profile photos) to a folder on disk
 * and returns the relative path to store in the database.
 * For a class project this is simpler than wiring up cloud storage (like S3);
 * in a real production system, uploads would usually go to cloud object storage instead.
 */
@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${foodrescue.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload folder: " + uploadRoot, e);
        }
    }

    /**
     * Saves the file under a subfolder (e.g. "documents" or "photos") with a random
     * name, so two people uploading "id.jpg" never overwrite each other.
     * Returns the relative path (subfolder/filename), or null if no file was sent.
     */
    public String store(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) return null;

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String safeName = UUID.randomUUID() + extension;

        try {
            Path folder = uploadRoot.resolve(subfolder);
            Files.createDirectories(folder);
            Path target = folder.resolve(safeName);
            Files.copy(file.getInputStream(), target);
            return subfolder + "/" + safeName;
        } catch (IOException e) {
            throw new IllegalStateException("Could not save the uploaded file. Please try again.", e);
        }
    }
}
