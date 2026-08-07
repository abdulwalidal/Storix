package com.storix.controller;

import com.storix.file.FileMetadata;
import com.storix.file.FileService;
import com.storix.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file) {
        FileMetadata metadata = fileService.upload(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(metadata);

    }



}
