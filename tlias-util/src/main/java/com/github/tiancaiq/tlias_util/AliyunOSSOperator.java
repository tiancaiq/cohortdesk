package com.github.tiancaiq.tlias_util;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
public class AliyunOSSOperator {

    private final AliyunOSSProperties aliyunOSSProperties;

    @Autowired
    public AliyunOSSOperator( AliyunOSSProperties aliyunOSSProperties ){
        this.aliyunOSSProperties = aliyunOSSProperties;
    }

    public String upload(byte[] content, String objectName) {

        String endpoint = aliyunOSSProperties.getEndpoint();    
        String bucketName = aliyunOSSProperties.getBucketName();
        String region = aliyunOSSProperties.getRegion();        

        OSS ossClient = null;

        try {
            // OSS_ACCESS_KEY_ID
            // OSS_ACCESS_KEY_SECRET
            EnvironmentVariableCredentialsProvider credentialsProvider =
                    CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

            ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
            conf.setSignatureVersion(SignVersion.V4);

            ossClient = OSSClientBuilder.create()
                    .endpoint(endpoint)
                    .credentialsProvider(credentialsProvider)
                    .clientConfiguration(conf)
                    .region(region)
                    .build();

            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));

        } catch (Exception e) {
            log.error("OSSUpload failed, objectName={}", objectName, e);

            throw new RuntimeException("OSS file upload failed", e);

        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // endpoint: https://oss-cn-hangzhou.aliyuncs.com
        return "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectName;
    }
}
