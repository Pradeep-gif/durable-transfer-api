package com.portfolio.transaction.api;

import java.time.Instant;
import java.util.Map;
import com.portfolio.transaction.domain.IdempotencyConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(IllegalArgumentException ex) {
        return Map.of("timestamp", Instant.now(), "status", 400, "error", ex.getMessage());
    }

    @ExceptionHandler(TransferNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> notFound(TransferNotFoundException ex) {
        return Map.of("timestamp", Instant.now(), "status", 404, "error", ex.getMessage());
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> idempotencyConflict(IdempotencyConflictException ex) {
        return Map.of("timestamp", Instant.now(), "status", 409, "error", ex.getMessage());
    }
}
