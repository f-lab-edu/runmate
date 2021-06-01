package com.runmate.service.exception;

public class NotFoundCrewException extends RuntimeException{
    public NotFoundCrewException() {
        super();
    }
    public NotFoundCrewException(String message) {
        super(message);
    }
}
