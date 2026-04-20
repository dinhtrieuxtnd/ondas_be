package com.example.ondas_be.application.exception;

public class AlbumNotFoundException extends RuntimeException {

    public AlbumNotFoundException(String message) {
        super(message);
    }
}
