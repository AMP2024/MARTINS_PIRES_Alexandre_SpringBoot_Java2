package com.amp.safetynetalerts.exception;

public class FirestationAddException extends RuntimeException {
    public FirestationAddException(String message) {
        super(message);
    }

    public FirestationAddException(String message, Throwable cause) {
        super(message, cause);
    }
}