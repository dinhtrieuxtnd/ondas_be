package com.example.ondas_be.infrastructure.storage;

import com.example.ondas_be.domain.repoport.FileStoragePort;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioStorageAdapter implements FileStoragePort {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    @Override
    public StoredFile upload(String folder, String filename, InputStream stream, long size, String contentType) {
        String objectKey = buildObjectKey(folder, filename);
        ensureBucketExists();

        try {
            PutObjectArgs args = PutObjectArgs.builder()
                .bucket(properties.getBucket())
                .object(objectKey)
                .stream(stream, size, -1)
                .contentType(contentType != null ? contentType : "application/octet-stream")
                .build();
            minioClient.putObject(args);
        } catch (Exception e) {
            throw new IllegalStateException("upload failed", e);
        }

        return new StoredFile(objectKey, buildPublicUrl(objectKey));
    }

    @Override
    public void delete(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(properties.getBucket())
                .object(objectKey)
                .build());
        } catch (Exception e) {
            throw new IllegalStateException("delete failed", e);
        }
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(properties.getBucket()).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
            }
        } catch (Exception e) {
            throw new IllegalStateException("bucket init failed", e);
        }
    }

    private String buildObjectKey(String folder, String filename) {
        String safeName = filename != null ? filename.replaceAll("\\s+", "_") : "file";
        return folder + "/" + UUID.randomUUID() + "_" + safeName;
    }

    private String buildPublicUrl(String objectKey) {
        String base = properties.getPublicUrl();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/" + properties.getBucket() + "/" + objectKey;
    }
}
