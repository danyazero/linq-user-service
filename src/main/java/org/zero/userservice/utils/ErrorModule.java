package org.zero.userservice.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zero.userservice.exception.RequestException;
import org.zero.userservice.model.ErrorResponse;

@Component
@ControllerAdvice
public class ErrorModule {
    @ExceptionHandler(RequestException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(RequestException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage() + " (Всього помилок " + ex.getBindingResult().getErrorCount() + ")"),
                HttpStatus.BAD_REQUEST
        );
    }
}
