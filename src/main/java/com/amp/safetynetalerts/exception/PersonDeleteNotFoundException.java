package com.amp.safetynetalerts.exception;

public class PersonDeleteNotFoundException extends RuntimeException {
    public PersonDeleteNotFoundException(String message) {
        super(message);
    }

    public PersonDeleteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}