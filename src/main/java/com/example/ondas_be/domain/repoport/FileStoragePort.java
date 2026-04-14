package com.example.ondas_be.domain.repoport;

import java.io.InputStream;

public interface FileStoragePort {

    record StoredFile(String objectKey, String url) {}

    StoredFile upload(String folder, String filename, InputStream stream, long size, String contentType);

    void delete(String objectKey);
}
