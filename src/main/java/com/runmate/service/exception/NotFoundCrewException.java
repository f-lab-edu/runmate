package com.runmate.service.exception;

public class NotFoundCrewException extends RuntimeException{
    public NotFoundCrewException() {
        super("cannot found for such crew");
    }
    public NotFoundCrewException(String message) {
        super(message);
    }
}
