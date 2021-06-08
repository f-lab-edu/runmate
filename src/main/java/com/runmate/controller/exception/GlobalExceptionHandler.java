package com.runmate.controller.exception;

import com.runmate.service.exception.*;
import com.runmate.utils.JsonWrapper;
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

    @ExceptionHandler({
            InvalidCodeException.class
    })
    public ResponseEntity<JsonWrapper> handleUnauthorizedException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(JsonWrapper.error(e.getMessage()));
    }

    @ExceptionHandler({
            UnAuthorizedException.class,
            InvalidValueException.class
    })
    public ResponseEntity<JsonWrapper> handleForbiddenException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(JsonWrapper.error(e.getMessage()));
    }

    @ExceptionHandler({
            NotFoundEntityException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<JsonWrapper> handleNotFoundException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(JsonWrapper.error(e.getMessage()));
    }

    //invalid Request Body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JsonWrapper> handleNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(JsonWrapper.error(createErrorMessage(e)));
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<JsonWrapper> handleURISyntaxException(URISyntaxException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(JsonWrapper.error("server error"));
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
