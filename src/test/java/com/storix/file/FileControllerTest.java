package com.storix.file;


import com.storix.controller.FileController;
import com.storix.dto.FileResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    FileController fileController;


    @Test
    void getFileById_shouldReturnFile() {

        FileResponse fileResponse = new FileResponse();
        fileResponse.setId(1L);
        fileResponse.setOriginalFileName("abc123.pdf");

        when(fileService.getFileMetaData(1L))
                .thenReturn(fileResponse);


        ResponseEntity<FileResponse> response = fileController.getFileById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fileResponse, response.getBody());


    }

    @Test
    void uploadFile_ShouldReturnCreated() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Hello Storix".getBytes()
        );

        FileResponse fileResponse = new FileResponse();
        fileResponse.setId(1L);
        fileResponse.setOriginalFileName("test.pdf");

        when(fileService.upload(file))
                .thenReturn(fileResponse);

        ResponseEntity<FileResponse> response = fileController.uploadFile(file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(fileResponse, response.getBody());


    }

    @Test
    void getAllFiles_shouldReturnFiles() {
        FileResponse fileResponse = new FileResponse();
        fileResponse.setId(1L);
        fileResponse.setOriginalFileName("test.pdf");

        List<FileResponse> addFiles = new ArrayList<>();
        addFiles.add(fileResponse);

        when(fileService.getAllFiles())
                .thenReturn(addFiles);


        ResponseEntity<List<FileResponse>> response = fileController.getAllFiles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(addFiles, response.getBody());








    }
}
