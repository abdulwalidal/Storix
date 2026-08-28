package com.storix.file;

import com.storix.dto.FileResponse;
import com.storix.exception.FileNotFoundException;
import com.storix.exception.InvalidFileException;
import com.storix.repository.FileMetadataRepository;
import com.storix.repository.UserRepository;
import com.storix.storage.StorageService;
import com.storix.user.User;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Set;

@Data
@Slf4j
@Service
public class FileService {

    @Value("${storix.storage.max-file-size}")
    private DataSize maxFileSize;


    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "text/plain"
    );

    private final StorageService storageService;
    private final FileMetadataRepository fileMetadataRepository;
    private final UserRepository userRepository;

    public FileService(StorageService storageService, FileMetadataRepository fileMetadataRepository, UserRepository userRepository) {
        this.storageService = storageService;
        this.fileMetadataRepository = fileMetadataRepository;
        this.userRepository = userRepository;
    }

    public FileResponse upload(MultipartFile file) {

        log.info("Upload request received. Filename: {}, Size: {} bytes",
                file.getOriginalFilename(),
                file.getSize());

        validate(file);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //  Get the currently authenticated user

        String email = auth.getName();

        // find the user in the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));



        String storedFileName = storageService.store(file);
        FileMetadata metadata = new FileMetadata();

        metadata.setOriginalFileName(file.getOriginalFilename());
        metadata.setStoredFileName(storedFileName);
        metadata.setContentType(file.getContentType());
        metadata.setSize(file.getSize());
        metadata.setUser(user);

        FileMetadata savedMetaData = fileMetadataRepository.save(metadata);

        return toFileResponse(savedMetaData);

    }

    public List<FileResponse> getAllFiles() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<FileMetadata> metadataList = fileMetadataRepository.findByUserEmail(email);

        // For every FileMetadata in this list, call toFileResponse() and put the resulting FileResponse into the new list.
        return metadataList.stream()
                .map(this::toFileResponse)
                .toList();



    }

    public Resource download(Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();




        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        return storageService.load(fileMetadata.getStoredFileName());
    }

    public FileResponse getFileMetaData (Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();


        FileMetadata fileMetadata =  fileMetadataRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        log.info("getFileMetaData is called");

        return toFileResponse(fileMetadata);

    }

    public void delete(Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        storageService.delete(fileMetadata.getStoredFileName());

        fileMetadataRepository.delete(fileMetadata);

    }



    public FileResponse update(Long id, MultipartFile newFile) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        log.info("User is trying to update the file");


        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        log.info("Update request received. Filename: {}, Size: {} bytes",
                newFile.getOriginalFilename(),
                newFile.getSize());

       validate(newFile);


        String newStoredFileName = storageService.replace(fileMetadata.getStoredFileName(), newFile);

        fileMetadata.setOriginalFileName(newFile.getOriginalFilename());
        fileMetadata.setStoredFileName(newStoredFileName);
        fileMetadata.setContentType(newFile.getContentType());
        fileMetadata.setSize(newFile.getSize());

        FileMetadata savedmetaData = fileMetadataRepository.save(fileMetadata);

        return toFileResponse(savedmetaData);


    }

    private void validate(MultipartFile file) {


        if(file.isEmpty()) {
            log.warn("Empty file received: {}", file.getOriginalFilename());

            throw new InvalidFileException("File cannot be empty");
        }

        if (file.getOriginalFilename() == null ||
                file.getOriginalFilename().isBlank()) {

            log.warn("file has no name");
            throw new InvalidFileException("File must have a name");
        }

        if (file.getSize() > maxFileSize.toBytes()) {
            log.warn("File rejected because it is too large: {} ({} bytes)",
                    file.getOriginalFilename(),
                    file.getSize());

            throw new InvalidFileException("File size exceeds the maximum allowed size");
        }

        if (file.getContentType() == null || file.getContentType().isBlank()) {
            log.warn("content type is missing for file: {}",
                    file.getOriginalFilename());

            throw new InvalidFileException("File content type is required");
        }

        if(!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) { // if the allowed-content-types list does NOT contain the file's content type, reject the file
            log.warn("File type not allowed {}", file.getContentType());
            throw new InvalidFileException("File type is not allowed");

        }

    }

    private FileResponse toFileResponse(FileMetadata fileMetadata) {
        FileResponse fileResponse = new FileResponse();
        fileResponse.setId(fileMetadata.getId());
        fileResponse.setOriginalFileName(fileMetadata.getOriginalFileName());
        fileResponse.setContentType(fileMetadata.getContentType());
        fileResponse.setSize(fileMetadata.getSize());

        return fileResponse;
    }




}

