package com.storix.storage;

import com.storix.exception.FileNotFoundException;
import com.storix.exception.StorageException;

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
            throw new StorageException("Could not initialize storage", e);
        }
    }

    // // Receives the uploaded file from the controller.
    @Override
    public String store(MultipartFile file) {


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
            throw new StorageException("Failed to store file.", e);
        }

        return storedFileName;
    }

    // Find that file in our storage folder and give it back as a Spring Resource.
    @Override
    public Resource load(String filename) {

        try {
            Path file = storageLocation.resolve(filename).normalize();

            if(!file.startsWith(storageLocation)) {
                throw new StorageException("Invalid file path");
            }

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new FileNotFoundException("File not found: " + filename);

        } catch (MalformedURLException e) {
            throw new StorageException(
                    "Could not load file " + filename,
                    e
            );
        }
    }

    @Override
    public void delete(String fileName) {

        try {
            Path file = storageLocation.resolve(fileName).normalize();

            if (!file.startsWith(storageLocation)) {
                throw new StorageException("Invalid file path");
            }

            if (!Files.exists(file)) {
                throw new FileNotFoundException("File not found: " + fileName);
            }

            Files.delete(file);

        } catch (IOException e) {
            throw new StorageException("Failed to delete file", e);
        }
    }

//    Store new
//    ↓
//    Delete old
//   ↓
//   Return new filename
    @Override
    public String replace(String oldFileName, MultipartFile newFile) {

        log.info("Replacing old files : {} ", oldFileName);

        String newStoredFile = store(newFile);
        delete(oldFileName);

        return newStoredFile;


    }


}