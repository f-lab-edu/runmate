package com.runmate.infra.exception;

public class ExternalServiceRequestException extends RuntimeException {
    public ExternalServiceRequestException() {
        super("cannot request to external service");
    }

    public ExternalServiceRequestException(String message) {
        super(message);
    }
}
