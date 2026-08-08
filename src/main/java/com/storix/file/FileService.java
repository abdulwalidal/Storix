package com.storix.file;

import com.storix.repository.FileMetadataRepository;
import com.storix.storage.StorageService;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
@Data
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
        metadata.setContentType(file.getContentType());
        metadata.setSize(file.getSize());

        return fileMetadataRepository.save(metadata);

    }

    public List<FileMetadata> getAllFiles() {

        return fileMetadataRepository.findAll();
    }

    public Resource download(Long id) {
        FileMetadata fileMetadata = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        return storageService.load(fileMetadata.getStoredFileName());
    }

    public FileMetadata getFileMetaData (Long id) {
        return fileMetadataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

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

