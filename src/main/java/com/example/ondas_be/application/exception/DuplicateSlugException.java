package com.example.ondas_be.application.exception;

public class DuplicateSlugException extends RuntimeException {

    public DuplicateSlugException(String message) {
        super(message);
    }
}
