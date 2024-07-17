package com.amp.safetynetalerts.exception;

public class FirestationDeleteException extends RuntimeException {
    public FirestationDeleteException(String message) {
        super(message);
    }

    public FirestationDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}