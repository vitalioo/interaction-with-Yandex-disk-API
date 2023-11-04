package com.example.exceptions;

public class RequestEntityTooLargeException extends RuntimeException {
    public RequestEntityTooLargeException(String message) {
        super(message);
    }
}
