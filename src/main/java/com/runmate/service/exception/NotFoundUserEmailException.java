package com.runmate.service.exception;

public class NotFoundUserEmailException extends RuntimeException {

    public NotFoundUserEmailException() {
        super("cannot found user for such email");
    }

    public NotFoundUserEmailException(String message) {
        super(message);
    }
}
