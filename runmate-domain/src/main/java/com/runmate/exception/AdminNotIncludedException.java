package com.runmate.exception;

public class AdminNotIncludedException extends RuntimeException {
    public AdminNotIncludedException() {
    }

    public AdminNotIncludedException(String message) {
        super(message);
    }
}
