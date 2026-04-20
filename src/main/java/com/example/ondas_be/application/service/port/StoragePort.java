package com.example.ondas_be.application.service.port;

import java.io.InputStream;

public interface StoragePort {

    String upload(String bucket, String objectName, InputStream inputStream, long size, String contentType);

    void delete(String bucket, String objectName);

    String getPublicUrl(String bucket, String objectName);

    String extractObjectName(String bucket, String url);
}
