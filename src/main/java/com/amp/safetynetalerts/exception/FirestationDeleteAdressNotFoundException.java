package com.amp.safetynetalerts.exception;

public class FirestationDeleteAdressNotFoundException extends RuntimeException {
    public FirestationDeleteAdressNotFoundException(String message) {
        super(message);
    }

    public FirestationDeleteAdressNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
public FirestationDeleteAdressNotFoundException() {
        super("Address not found in firestations");
    }
}