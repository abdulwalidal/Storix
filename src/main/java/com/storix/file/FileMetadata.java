package com.storix.file;

import com.storix.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long size;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
