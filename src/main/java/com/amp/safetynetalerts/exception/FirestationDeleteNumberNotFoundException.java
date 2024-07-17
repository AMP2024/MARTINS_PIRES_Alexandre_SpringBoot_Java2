package com.amp.safetynetalerts.exception;

public class FirestationDeleteNumberNotFoundException extends RuntimeException {
    public FirestationDeleteNumberNotFoundException(String message) {
        super(message);
    }

    public FirestationDeleteNumberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FirestationDeleteNumberNotFoundException() {
        super("Station number not found in firestations");
    }
}