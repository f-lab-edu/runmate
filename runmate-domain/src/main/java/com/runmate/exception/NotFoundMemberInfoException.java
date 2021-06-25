package com.runmate.exception;

public class NotFoundMemberInfoException extends NotFoundEntityException {
    public NotFoundMemberInfoException() {
    }

    public NotFoundMemberInfoException(String message) {
        super(message);
    }
}
