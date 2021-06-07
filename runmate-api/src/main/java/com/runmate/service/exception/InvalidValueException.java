package com.runmate.service.exception;

public class InvalidValueException extends BusinessException {
    public InvalidValueException() {
    }

    public InvalidValueException(String message) {
        super(message);
    }
}
