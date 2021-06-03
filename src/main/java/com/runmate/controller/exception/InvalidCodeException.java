package com.runmate.controller.exception;

public class InvalidCodeException extends RuntimeException{
    public InvalidCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
