package com.amp.safetynetalerts.exception;

public class MedicalRecordAddException extends RuntimeException {
    public MedicalRecordAddException(String message) {
        super(message);
    }

    public MedicalRecordAddException(String message, Throwable cause) {
        super(message, cause);
    }
}