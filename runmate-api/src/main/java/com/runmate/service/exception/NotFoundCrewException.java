package com.runmate.service.exception;

public class NotFoundCrewException extends NotFoundEntityException{
    public NotFoundCrewException() {
        super("cannot found for such crew");
    }
    public NotFoundCrewException(String message) {
        super(message);
    }
}
