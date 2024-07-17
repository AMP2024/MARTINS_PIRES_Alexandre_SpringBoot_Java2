package com.amp.safetynetalerts.exception;

public class MedicalRecordUpdateException extends RuntimeException {
    public MedicalRecordUpdateException(String message) {
        super(message);
    }

    public MedicalRecordUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

}