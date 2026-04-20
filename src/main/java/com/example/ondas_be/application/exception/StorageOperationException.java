package com.example.ondas_be.application.exception;

public class StorageOperationException extends RuntimeException {

    public StorageOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
