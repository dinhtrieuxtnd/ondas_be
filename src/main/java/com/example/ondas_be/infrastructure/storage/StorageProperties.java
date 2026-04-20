package com.example.ondas_be.infrastructure.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "storage.minio")
public class StorageProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketAudio;
    private String bucketImage;
}
