package com.runmate.exception;

public class NotFoundCrewJoinRequestException extends NotFoundEntityException {
    public NotFoundCrewJoinRequestException(String message) {
        super(message);
    }

    public NotFoundCrewJoinRequestException() {
        super();
    }
}
