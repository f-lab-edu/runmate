package com.runmate.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
    public static final String INVALID_REQUEST_BODY_MESSAGE = "invalid Request Body";
    public static final String INVALID_CODE_MESSAGE = "failed to get access token:invalid Code";

    @ExceptionHandler(InvalidCodeException.class)
    public ResponseEntity<String> handleInvalidCodeException(InvalidCodeException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(INVALID_CODE_MESSAGE);
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<String> handleURISyntaxException(URISyntaxException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("server error");
    }

    //invalid Request Body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(INVALID_REQUEST_BODY_MESSAGE);
    }
}
