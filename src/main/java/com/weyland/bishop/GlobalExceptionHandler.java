package com.weyland.bishop;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex, WebRequest request) {
        
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.joining("; "));
        
        return ResponseEntity.badRequest().body(
            new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errorMsg,
                request.getDescription(false).replace("uri=", ""))
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
        HttpMessageNotReadableException ex, WebRequest request) {
        
        String errorMsg = "Invalid request format";
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                errorMsg = String.format("Invalid value for %s: '%s'. Accepted values: %s",
                    ife.getPath().get(0).getFieldName(),
                    ife.getValue(),
                    Arrays.toString(ife.getTargetType().getEnumConstants()));
            }
        }
        
        return ResponseEntity.badRequest().body(
            new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request",
                errorMsg,
                request.getDescription(false).replace("uri=", ""))
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleQueueOverflow(
        IllegalStateException ex, WebRequest request) {
        
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
            new ErrorResponse(
                Instant.now(),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Queue Overflow",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""))
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        return ResponseEntity.internalServerError().body(
            new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Error",
                "Android malfunction detected: " + ex.getMessage(),
                request.getDescription(false).replace("uri=", ""))
        );
    }
}