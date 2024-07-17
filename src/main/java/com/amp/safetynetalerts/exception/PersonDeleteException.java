package com.amp.safetynetalerts.exception;

public class PersonDeleteException extends RuntimeException {
    public PersonDeleteException(String message) {
        super(message);
    }

    public PersonDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

}