package com.amp.safetynetalerts.exception;

public class FirestationUpdateException extends RuntimeException {
    public FirestationUpdateException(String message) {
        super(message);
    }

    public FirestationUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}