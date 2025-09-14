package com.http.exception;

public class InvalidHttpRequestException extends RuntimeException {

    // Constructor that accepts a message
    public InvalidHttpRequestException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public InvalidHttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}