package com.runmate.exception;

public class CurrentIsNotRunningTimeException extends RuntimeException {
    public CurrentIsNotRunningTimeException() {
    }

    public CurrentIsNotRunningTimeException(String message) {
        super(message);
    }
}
