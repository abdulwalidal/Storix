package com.storix.repository;

import com.storix.file.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    Optional<FileMetadata> findByIdAndUserEmail(Long fileId, String email);
    List<FileMetadata> findByUserEmail(String email);

}