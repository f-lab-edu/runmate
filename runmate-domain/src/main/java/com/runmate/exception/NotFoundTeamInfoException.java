package com.runmate.exception;

public class NotFoundTeamInfoException extends NotFoundEntityException {
    public NotFoundTeamInfoException() {
    }

    public NotFoundTeamInfoException(String message) {
        super(message);
    }
}
