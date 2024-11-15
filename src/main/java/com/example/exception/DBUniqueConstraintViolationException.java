package com.example.exception;

public class DBUniqueConstraintViolationException extends RuntimeException {

    public DBUniqueConstraintViolationException(String message) {
        super(message);
    }
}