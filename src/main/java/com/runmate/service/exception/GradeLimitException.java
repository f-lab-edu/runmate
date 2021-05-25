package com.runmate.service.exception;

public class GradeLimitException extends RuntimeException{
    public GradeLimitException(String message) {
        super(message);
    }
}
