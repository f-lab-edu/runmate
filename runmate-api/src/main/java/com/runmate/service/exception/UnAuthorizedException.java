package com.runmate.service.exception;

public class UnAuthorizedException extends BusinessException {
    public UnAuthorizedException() {
        super();
    }

    public UnAuthorizedException(String message) {
        super(message);
    }
}
