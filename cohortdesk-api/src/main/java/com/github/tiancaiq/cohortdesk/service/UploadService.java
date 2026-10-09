package com.github.tiancaiq.cohortdesk.service;

import org.springframework.web.multipart.MultipartFile;


public interface UploadService {
    String upload( MultipartFile file );
}
