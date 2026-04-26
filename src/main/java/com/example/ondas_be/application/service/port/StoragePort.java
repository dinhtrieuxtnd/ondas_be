package com.example.ondas_be.application.service.port;

import java.io.InputStream;

public interface StoragePort {

    String upload(String bucket, String objectName, InputStream inputStream, long size, String contentType);

    void delete(String bucket, String objectName);

    String getPublicUrl(String bucket, String objectName);

    String extractObjectName(String bucket, String url);

    /**
     * Opens a ranged input stream for an object in storage.
     * @param bucket     the bucket name
     * @param objectName the object key
     * @param offset     byte offset to start reading from
     * @param length     number of bytes to read; -1 to read until end of object
     * @return InputStream positioned at the given offset
     */
    InputStream getObjectStream(String bucket, String objectName, long offset, long length);
}
