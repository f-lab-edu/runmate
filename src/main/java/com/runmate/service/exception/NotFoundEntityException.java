package com.runmate.service.exception;

public class NotFoundEntityException extends BusinessException {
    public NotFoundEntityException() {
    }

    public NotFoundEntityException(String message) {
        super(message);
    }
}
