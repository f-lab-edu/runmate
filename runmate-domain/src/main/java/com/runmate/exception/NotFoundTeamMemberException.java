package com.runmate.exception;

public class NotFoundTeamMemberException extends NotFoundEntityException{
    public NotFoundTeamMemberException() {
    }

    public NotFoundTeamMemberException(String message) {
        super(message);
    }
}
