package com.example.exceptions;

public class NotAcceptableException extends RuntimeException {
    public NotAcceptableException(String message) {
        super(message);
    }
}
