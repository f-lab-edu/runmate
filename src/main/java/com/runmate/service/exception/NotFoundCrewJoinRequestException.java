package com.runmate.service.exception;

public class NotFoundCrewJoinRequestException extends RuntimeException{
    public NotFoundCrewJoinRequestException(String message) {
        super(message);
    }

    public NotFoundCrewJoinRequestException() {
        super();
    }
}
