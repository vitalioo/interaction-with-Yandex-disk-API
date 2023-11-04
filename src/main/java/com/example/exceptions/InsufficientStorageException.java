package com.example.exceptions;

public class InsufficientStorageException extends RuntimeException {
    public InsufficientStorageException(String message) {
        super(message);
    }
}
