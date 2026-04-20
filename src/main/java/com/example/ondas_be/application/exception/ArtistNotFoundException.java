package com.example.ondas_be.application.exception;

public class ArtistNotFoundException extends RuntimeException {

    public ArtistNotFoundException(String message) {
        super(message);
    }
}
