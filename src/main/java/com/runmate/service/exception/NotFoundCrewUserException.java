package com.runmate.service.exception;

public class NotFoundCrewUserException extends NotFoundEntityException {
    public NotFoundCrewUserException(String message) {
        super(message);
    }

    public NotFoundCrewUserException() {
    }
}
