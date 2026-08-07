package com.storix.file;

import com.storix.repository.FileMetadataRepository;
import com.storix.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private final StorageService storageService;
    private final FileMetadataRepository fileMetadataRepository;

    public FileService(StorageService storageService, FileMetadataRepository fileMetadataRepository) {
        this.storageService = storageService;
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public FileMetadata upload(MultipartFile file) {

        String storedFileName = storageService.store(file);
        FileMetadata metadata = new FileMetadata();

        metadata.setOriginalFileName(file.getOriginalFilename());
        metadata.setStoredFileName(storedFileName);
        metadata.setStoredFileName(file.getContentType());
        metadata.setSize(file.getSize());

        return fileMetadataRepository.save(metadata);

    }


}

