package com.runmate.service.exception;

public class NotFoundCrewUserException extends RuntimeException {
    public NotFoundCrewUserException(String message) {
        super(message);
    }

    public NotFoundCrewUserException() {
    }
}
