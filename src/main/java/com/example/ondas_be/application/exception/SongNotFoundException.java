package com.example.ondas_be.application.exception;

public class SongNotFoundException extends RuntimeException {

    public SongNotFoundException(String message) {
        super(message);
    }
}
