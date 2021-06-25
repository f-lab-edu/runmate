package com.runmate.exception;

public class NotFoundTeamException extends NotFoundEntityException {
    public NotFoundTeamException() {
    }

    public NotFoundTeamException(String message) {
        super(message);
    }
}
