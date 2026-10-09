package com.github.tiancaiq.cohortdesk.controller;

import com.github.tiancaiq.cohortdesk.model.Result;
import com.github.tiancaiq.cohortdesk.annotation.LogOperation;
import com.github.tiancaiq.cohortdesk.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping( "/upload" )
public class UploadController {
    private final UploadService uploadService;

    @Autowired
    public UploadController( UploadService uploadService ){
        this.uploadService = uploadService;
    }

    @LogOperation
    @PostMapping
    public Result<String> upload( @RequestParam( "file" ) MultipartFile file) {
        log.info("Upload File: {}", file );
        String url = uploadService.upload(file);
        return Result.success(url);
    }
}