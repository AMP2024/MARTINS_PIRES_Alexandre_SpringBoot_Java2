package com.amp.safetynetalerts.exception;

public class PersonAddException extends RuntimeException {
    public PersonAddException(String message) {
        super(message);
    }

    public PersonAddException(String message, Throwable cause) {
        super(message, cause);
    }
}