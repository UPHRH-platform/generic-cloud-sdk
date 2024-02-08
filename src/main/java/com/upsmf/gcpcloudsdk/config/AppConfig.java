package com.upsmf.gcpcloudsdk.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppConfig {

    @Value("${application.bucket.name}")
    private String bucketName;
    @Value("${application.bucket.name.private}")
    private String privateBucketName;
    @Value(("${spring.servlet.multipart.max-file-size}"))
    private String fileSize;
    @Value("${cloud.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.credentials.secret-key}")
    private String accessSecret;
    @Value("${aws.region}")
    private String awsRegion;

}
