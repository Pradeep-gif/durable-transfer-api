package com.portfolio.transaction.domain;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException(String key) {
        super("Idempotency key was already used with a different request: " + key);
    }
}
