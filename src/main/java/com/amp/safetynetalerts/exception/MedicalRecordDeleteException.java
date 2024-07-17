package com.amp.safetynetalerts.exception;

public class MedicalRecordDeleteException extends RuntimeException {
    public MedicalRecordDeleteException(String message) {
        super(message);
    }

    public MedicalRecordDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}