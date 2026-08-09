package com.storix.file;

import com.storix.exception.FileNotFoundException;
import com.storix.exception.InvalidFileException;
import com.storix.repository.FileMetadataRepository;
import com.storix.storage.StorageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@Data
public class FileService {

    private static final long MAX_FILE_SIZE = 10 * 1024; // 10 KB (for testing)

    private final StorageService storageService;
    private final FileMetadataRepository fileMetadataRepository;

    public FileService(StorageService storageService, FileMetadataRepository fileMetadataRepository) {
        this.storageService = storageService;
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public FileMetadata upload(MultipartFile file) {


        log.info("Upload request received. Filename: {}, Size: {} bytes",
                file.getOriginalFilename(),
                file.getSize());

        if(file.isEmpty()) {
            log.warn("Empty file received: {}", file.getOriginalFilename());

            throw new InvalidFileException("File cannot be empty");
        }

        if (file.getOriginalFilename() == null ||
                file.getOriginalFilename().isBlank()) {

            log.warn("Upload rejected: file has no name");
            throw new InvalidFileException("File must have a name");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("File rejected because it is too large: {} ({} bytes)",
                    file.getOriginalFilename(),
                    file.getSize());

            throw new InvalidFileException("File size cannot exceed 10 KB");
        }

        if (file.getContentType() == null || file.getContentType().isBlank()) {
            log.warn("Upload rejected: content type is missing for file: {}",
                    file.getOriginalFilename());

            throw new InvalidFileException("File content type is required");
        }




        String storedFileName = storageService.store(file);
        FileMetadata metadata = new FileMetadata();

        metadata.setOriginalFileName(file.getOriginalFilename());
        metadata.setStoredFileName(storedFileName);
        metadata.setContentType(file.getContentType());
        metadata.setSize(file.getSize());

        return fileMetadataRepository.save(metadata);

    }

    public List<FileMetadata> getAllFiles() {

        return fileMetadataRepository.findAll();
    }

    public Resource download(Long id) {
        FileMetadata fileMetadata = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        return storageService.load(fileMetadata.getStoredFileName());
    }

    public FileMetadata getFileMetaData (Long id) {
        return fileMetadataRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

    }

    public void delete(Long id) {
        FileMetadata fileMetadata = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        storageService.delete(fileMetadata.getStoredFileName());

        fileMetadataRepository.delete(fileMetadata);

    }

    public FileMetadata update(Long id, MultipartFile newFile) {

        FileMetadata fileMetadata = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String newStoredFileName = storageService.replace(fileMetadata.getStoredFileName(), newFile);

        fileMetadata.setOriginalFileName(newFile.getOriginalFilename());
        fileMetadata.setStoredFileName(newStoredFileName);
        fileMetadata.setContentType(newFile.getContentType());
        fileMetadata.setSize(newFile.getSize());

        return fileMetadataRepository.save(fileMetadata);






    }




}

