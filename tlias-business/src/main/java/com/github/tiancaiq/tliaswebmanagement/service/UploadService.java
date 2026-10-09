package com.github.tiancaiq.tliaswebmanagement.service;

import org.springframework.web.multipart.MultipartFile;


public interface UploadService {
    String upload( MultipartFile file );
}
