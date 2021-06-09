package com.runmate.exception;

public class NotFoundCrewUserException extends NotFoundEntityException {
    public NotFoundCrewUserException(String message) {
        super(message);
    }

    public NotFoundCrewUserException() {
    }
}
