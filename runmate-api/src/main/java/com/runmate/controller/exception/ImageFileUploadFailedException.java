package com.runmate.controller.exception;

public class ImageFileUploadFailedException extends RuntimeException {
    public ImageFileUploadFailedException() {
    }

    public ImageFileUploadFailedException(String s) {
        super(s);
    }
}
