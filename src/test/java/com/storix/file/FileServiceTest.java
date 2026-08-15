package com.storix.file;

import com.storix.dto.FileResponse;
import com.storix.exception.FileNotFoundException;
import com.storix.exception.InvalidFileException;
import com.storix.repository.FileMetadataRepository;
import com.storix.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private FileMetadataRepository fileMetadataRepository;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService.setMaxFileSize(DataSize.ofKilobytes(10));
    }

    @Test
    void uploadFile_shouldUploadSuccessfully() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Hello Storix".getBytes()
        );

        when(storageService.store(file))
                .thenReturn("abc123.pdf");

        FileMetadata metadata = new FileMetadata();
        metadata.setId(1L);
        metadata.setOriginalFileName("test.pdf");
        metadata.setStoredFileName("abc123.pdf");
        metadata.setContentType("application/pdf");
        metadata.setSize(file.getSize());

        when(fileMetadataRepository.save(any(FileMetadata.class)))
                .thenReturn(metadata);

        FileResponse response = fileService.upload(file);

        assertEquals("test.pdf", response.getOriginalFileName());

        verify(storageService).store(file);
        verify(fileMetadataRepository).save(any(FileMetadata.class));
    }


    @Test
    void uploadFile_shouldRejectEmptyFile() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[0]
        );

        assertThrows(
                InvalidFileException.class,
                () -> fileService.upload(file)
        );
    }


    @Test
    void uploadFile_shouldRejectFileThatIsTooLarger() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[11 * 1024]
        );

        assertThrows(
                InvalidFileException.class,
                ()-> fileService.upload(file)
        );


    }

    @Test
    void getFileMetaData_shouldReturnFile() {

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(1L);
        fileMetadata.setOriginalFileName("test.pdf");

        when(fileMetadataRepository.findById(1L))
                .thenReturn(Optional.of(fileMetadata));

        FileResponse fileResponse = fileService.getFileMetaData(1L);

        assertEquals(1L, fileResponse.getId());

    }

    @Test
    void getFileMetaData_shouldThrowWhenFileNotFound() {
        when(fileMetadataRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                FileNotFoundException.class,
                ()-> fileService.getFileMetaData(1L)
        );


    }



}