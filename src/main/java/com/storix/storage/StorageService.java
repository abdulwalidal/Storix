package com.storix.storage;

import org.springframework.web.multipart.MultipartFile;
// MultipartFile represents a file uploaded through an HTTP request.
// in Postman and sends it to Storix.
// Spring converts the uploaded file into a Java object of type:
// this object gives us useful methods

public interface StorageService {
    String store (MultipartFile file);
}
