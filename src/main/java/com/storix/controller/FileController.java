package com.storix.controller;

import com.storix.dto.FileResponse;
import com.storix.file.FileMetadata;
import com.storix.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {


    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }


    // // accepts file uploads using multipart/form-data.
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileResponse fileResponse  = fileService.upload(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileResponse);

    }

    @GetMapping
    public ResponseEntity<List<FileResponse>>getAllFiles() {
       List<FileResponse> files = fileService.getAllFiles();
       return ResponseEntity.ok(files);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {

        FileResponse fileResponse = fileService.getFileMetaData(id); // Give me the information about file ID 4
        Resource resource = fileService.download(id); // This gets the actual file

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileResponse.getOriginalFileName() + "\""
                )
                .contentType(MediaType.parseMediaType(fileResponse.getContentType()))
                .body(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileResponse> getFileById(@PathVariable Long id) {

        long start = System.nanoTime();

        FileResponse response = fileService.getFileMetaData(id);

        long end = System.nanoTime();

        log.info("Request took {} ms",
                (end - start) / 1_000_000.0);

        // FileResponse fileResponse = fileService.getFileMetaData(id);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping(
            path = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FileResponse> updateFile (
            @PathVariable Long id,
            @RequestParam("updatefile") MultipartFile file) {
        log.info("UPDATE request received for file ID: {}", id);

        FileResponse fileResponse = fileService.update(id, file);

        return ResponseEntity.ok(fileResponse);

    }





}
