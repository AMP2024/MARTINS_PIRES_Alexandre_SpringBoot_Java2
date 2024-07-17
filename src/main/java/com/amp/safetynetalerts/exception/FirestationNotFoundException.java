package com.amp.safetynetalerts.exception;

public class FirestationNotFoundException extends RuntimeException {
    public FirestationNotFoundException(String message) {
        super(message);
    }

    public FirestationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
