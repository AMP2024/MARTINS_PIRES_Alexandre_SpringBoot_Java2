package com.amp.safetynetalerts.exception;

public class MedicalRecordNotFoundException extends RuntimeException {
    public MedicalRecordNotFoundException(String message) {
        super(message);
    }

    public MedicalRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}