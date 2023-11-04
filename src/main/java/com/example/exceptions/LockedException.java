package com.example.exceptions;

public class LockedException extends RuntimeException {
    public LockedException(String message) {
        super(message);
    }
}
