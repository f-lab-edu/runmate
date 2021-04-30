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
                .body(createErrorMessage(e));
    }

    private String createErrorMessage(MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < e.getErrorCount(); i++) {
            sb.append(e.getBindingResult().getFieldErrors().get(i).getField() + ":"
                    + e.getBindingResult().getAllErrors().get(i).getDefaultMessage() + ";");
        }
        return sb.toString();
    }
}
