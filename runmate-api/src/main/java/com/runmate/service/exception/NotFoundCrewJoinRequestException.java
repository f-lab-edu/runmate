package com.runmate.service.exception;

public class NotFoundCrewJoinRequestException extends NotFoundEntityException{
    public NotFoundCrewJoinRequestException(String message) {
        super(message);
    }

    public NotFoundCrewJoinRequestException() {
        super();
    }
}
