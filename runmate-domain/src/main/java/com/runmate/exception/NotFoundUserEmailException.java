package com.runmate.exception;

public class NotFoundUserEmailException extends NotFoundEntityException {

    public NotFoundUserEmailException() {
        super("cannot found user for such email");
    }

    public NotFoundUserEmailException(String message) {
        super(message);
    }
}
