package com.amp.safetynetalerts.exception;

public class PersonUpdateException extends RuntimeException {
    public PersonUpdateException(String message) {
        super(message);
    }

    public PersonUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}