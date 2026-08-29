package com.storix.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileResponse implements Serializable{

    private Long id;
    private String originalFileName;
    private String contentType;
    private Long size;
}
