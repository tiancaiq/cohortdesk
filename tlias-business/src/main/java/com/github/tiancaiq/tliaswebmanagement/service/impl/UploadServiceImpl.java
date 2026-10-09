package com.github.tiancaiq.tliaswebmanagement.service.impl;

import com.github.tiancaiq.tlias_util.AliyunOSSOperator;
import com.github.tiancaiq.tliaswebmanagement.exception.BusinessException;
import com.github.tiancaiq.tliaswebmanagement.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional( readOnly = true )
public class UploadServiceImpl implements UploadService {


    private static final Set<String> ALLOWED_FILE_EXTENSIONS = Set.of(".jpg", ".png", ".jpeg");



    private final AliyunOSSOperator aliyunOSSOperator;



    @Autowired
    public UploadServiceImpl(AliyunOSSOperator aliyunOSSOperator) {
        this.aliyunOSSOperator = aliyunOSSOperator;
    }


    @Override
    public String upload(MultipartFile file) {

        String extension = extractAndValidateExtension(file);

        String objectName = buildPath("avatar", extension);

        try {
            return aliyunOSSOperator.upload(file.getBytes(), objectName);

        } catch ( IOException e) {
            throw new BusinessException("Could not read the file");

        } catch (Exception e) {
            throw new BusinessException("Upload failed. Try again later");
        }
    }



    private String extractAndValidateExtension(MultipartFile file) {
        if ( file == null ){
            throw new BusinessException( "Select a file to upload." );
        }

        if ( file.isEmpty() ) {
            throw new BusinessException( "The file cannot be empty" );
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("Invalid file name: missing extension");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        if (!ALLOWED_FILE_EXTENSIONS.contains(extension)) {
            throw new BusinessException("Unsupported file type. Allowed types: jpg, png, jpeg");
        }

        return extension;
    }


    private String buildPath(String bizType, String extension) {

        String datePath = LocalDate.now()
                .format( DateTimeFormatter.ofPattern("yyyy/MM"));

        String fileName = UUID.randomUUID()
                .toString()
                .replace("-", "") + extension;

        return bizType + "/" + datePath + "/" + fileName;
    }
}
