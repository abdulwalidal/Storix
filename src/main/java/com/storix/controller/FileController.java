package com.storix.controller;

import com.storix.dto.FileResponse;
import com.storix.file.FileMetadata;
import com.storix.file.FileService;
import com.storix.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {


    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }


//    private final StorageService storageService;
//
//    public FileController(StorageService storageService) {
//        this.storageService = storageService;
//    }


    @PostMapping
    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileResponse fileResponse  = fileService.upload(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileResponse);

    }

    @GetMapping
    public ResponseEntity<List<FileMetadata>>getAllFiles() {
       List<FileMetadata> files = fileService.getAllFiles();
       return ResponseEntity.ok(files);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {

        FileMetadata fileMetadata = fileService.getFileMetaData(id); // Give me the information about file ID 4
        Resource resource = fileService.download(id); // This gets the actual file

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileMetadata.getOriginalFileName() + "\""
                )
                .contentType(MediaType.parseMediaType(fileMetadata.getContentType()))
                .body(resource);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("{id}")
    public ResponseEntity<FileMetadata> updateFile (
            @PathVariable Long id,
            @RequestParam("updatefile") MultipartFile file) {
        log.info("UPDATE request received for file ID: {}", id);

        FileMetadata updatedFile = fileService.update(id, file);

        return ResponseEntity.ok(updatedFile);

    }





}
