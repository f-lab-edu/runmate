package com.runmate.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidCodeException.class)
    public ResponseEntity<String>handleInvalidCodeException(InvalidCodeException e){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("failed to get access token:invalid Code");
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<String>handleURISyntaxException(URISyntaxException e){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("server error");
    }
}
