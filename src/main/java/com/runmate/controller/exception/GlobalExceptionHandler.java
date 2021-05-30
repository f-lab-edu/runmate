package com.runmate.controller.exception;

import com.runmate.service.exception.*;
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
    public ResponseEntity<String> handleUnauthorizedException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }

    @ExceptionHandler({
            UnAuthorizedException.class,
            BelongToSomeCrewException.class,
            GradeLimitException.class
    })
    public ResponseEntity<String> handleForbiddenException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }

    @ExceptionHandler({
            NotFoundCrewUserException.class,
            NotFoundCrewException.class,
            NotFoundCrewJoinRequestException.class
    })
    public ResponseEntity<String> handleNotFoundException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    //invalid Request Body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createErrorMessage(e));
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<String> handleURISyntaxException(URISyntaxException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("server error");
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
