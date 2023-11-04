package com.example.exceptions;

public class NotAvailableAPIException extends RuntimeException {
    public NotAvailableAPIException(String message) {
        super(message);
    }
}
