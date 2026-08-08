package com.storix.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
public class LocalStorageService implements StorageService {

    private final Path storageLocation; // It represents a folder path, receives its value once and cannot later be replaced.


    public LocalStorageService(
            @Value("${storix.storage.location}") String storageLocation
    ) {
        this.storageLocation = Path.of(storageLocation)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    // // Receives the uploaded file from the controller.
    @Override
    public String store(MultipartFile file) {
        if(file.isEmpty()) {
            throw new RuntimeException("Cannot store an empty file");
        }

        String uniqueFileName = UUID.randomUUID().toString(); // // Generate a unique filename to avoid duplicate file names.

        String originalFilename = file.getOriginalFilename(); // // Get the original filename uploaded by the user.
        int dotIndex = originalFilename.lastIndexOf("."); // // Find the position of the last '.' in the filename.
        String extension = "";

        if(dotIndex != -1) {
            extension = originalFilename.substring(dotIndex);
        }

        String storedFileName = uniqueFileName + extension;
        Path destinationFile = storageLocation.resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), destinationFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }

        return storedFileName;
    }

    // Find that file in our storage folder and give it back as a Spring Resource.
    @Override
    public Resource load(String filename) { // We receive the filename (example "fileName = "abc123.png"

        try {
            // Build the path to the requested file inside our storage folder.
            Path file = storageLocation.resolve(filename).normalize();

            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new RuntimeException("file not found " + filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not load file " + filename,e);
        }

    }

    @Override
    public void delete(String fileName) {

        try {
            Path file = storageLocation.resolve(fileName);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete that file", e);
        }

    }

    // - Delete the old physical file.
    // - Store the new file.
    // - Return the new stored filename.
    @Override
    public String replace(String oldFileName, MultipartFile newFile) {

        log.info("Replacing old files : {} ", oldFileName);

        delete(oldFileName);
        return store(newFile);

    }


}