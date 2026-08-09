package com.storix.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleFileNotFound(
            FileNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFile(
            InvalidFileException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", exception.getMessage()));
    }

//    @ExceptionHandler(MissingServletRequestParameterException.class)
//    public ResponseEntity<Map<String, String>> handleMissingParameter(
//            MissingServletRequestParameterException exception) {
//
//        log.warn("Missing request parameter: {}", exception.getParameterName());
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(Map.of(
//                        "error", "MISSING_PARAMETER",
//                        "message", "Please provide a file"
//                ));
//    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, String>> handleMultipartException(
            MultipartException exception) {

        log.warn("Invalid multipart request: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "INVALID_FILE_UPLOAD",
                        "message", "Please send the request as multipart/form-data"
                ));
    }


    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> handleMissingFilePart(
            MissingServletRequestPartException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "MISSING_FILE",
                        "message", "Please provide a file"
                ));
    }
}
