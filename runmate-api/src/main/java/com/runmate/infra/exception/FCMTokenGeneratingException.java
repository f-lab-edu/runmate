package com.runmate.infra.exception;

public class FCMTokenGeneratingException extends RuntimeException {
    public FCMTokenGeneratingException() {
        super("cannot create FCM token from given resource");
    }

    public FCMTokenGeneratingException(String message) {
        super(message);
    }
}
