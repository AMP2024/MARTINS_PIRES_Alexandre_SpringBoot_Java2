package com.amp.safetynetalerts.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Logger l'exception
        logger.error("Handling IllegalArgumentException: ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {PersonNotFoundException.class})
    public ResponseEntity<String> handlePersonNotFoundException(PersonNotFoundException ex) {
        logger.error("Handling PersonNotFoundException: ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = {FirestationNotFoundException.class})
    public ResponseEntity<String> handleFirestationNotFoundException(FirestationNotFoundException ex) {
        logger.error("Handling FirestationNotFoundException: ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {MedicalRecordNotFoundException.class})
    public ResponseEntity<String> handleMedicalRecordNotFoundException(MedicalRecordNotFoundException ex) {
        logger.error("Handling MedicalRecordNotFoundException: ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Void> handleOtherExceptions(Exception ex) {
        logger.error("An unexpected error occurred: ", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {PersonAddException.class})
    public ResponseEntity<String> handlePersonAddException(PersonAddException e) {
        logger.error("Handling PersonAddException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(value = {PersonUpdateException.class})
    public ResponseEntity<String> handlePersonUpdateException(PersonUpdateException e) {
        logger.error("Handling PersonUpdateException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {PersonDeleteException.class})
    public ResponseEntity<String> handlePersonDeleteException(PersonDeleteException e) {
        logger.error("Handling PersonDeleteException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {PersonDeleteNotFoundException.class})
    public ResponseEntity<String> handlePersonDeleteNotFoundException(PersonDeleteNotFoundException e) {
        logger.error("Handling PersonDeleteNotFoundException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {FirestationAddException.class})
    public ResponseEntity<String> handleFirestationAddException(FirestationAddException e) {
        logger.error("Handling FirestationAddException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {FirestationUpdateException.class})
    public ResponseEntity<String> handleFirestationUpdateException(FirestationUpdateException e) {
        logger.error("Handling FirestationUpdateException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(value = {FirestationDeleteException.class})
    public ResponseEntity<String> handleFirestationDeleteException(FirestationDeleteException e) {
        logger.error("Handling FirestationDeleteException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {FirestationDeleteAdressNotFoundException.class})
    public ResponseEntity<String> handleFirestationDeleteAdressNotFoundException(FirestationDeleteAdressNotFoundException e) {
        logger.error("Handling FirestationDeleteAdressNotFoundException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {FirestationDeleteNumberNotFoundException.class})
    public ResponseEntity<String> handleFirestationDeleteNumberNotFoundException(FirestationDeleteNumberNotFoundException e) {
        logger.error("Handling FirestationDeleteNumberNotFoundException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {MedicalRecordAddException.class})
    public ResponseEntity<String> handleMedicalRecordAddException(MedicalRecordAddException e) {
        logger.error("Handling MedicalRecordAddException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {MedicalRecordUpdateException.class})
    public ResponseEntity<String> handleMedicalRecordUpdateException(MedicalRecordUpdateException e) {
        logger.error("Handling MedicalRecordUpdateException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {MedicalRecordDeleteException.class})
    public ResponseEntity<String> handleMedicalRecordDeleteException(MedicalRecordDeleteException e) {
        logger.error("Handling MedicalRecordDeleteException: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {AddressNotFoundException.class})
    public ResponseEntity<String> handleAddressNotFoundException(AddressNotFoundException ex) {
        logger.error("Handling AddressNotFoundException: ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

   /*
    @ExceptionHandler(value = {InvalidDataException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidDataException(InvalidDataException ex) {
        logger.error("Handling InvalidDataException: ", ex);
        return ex.getMessage();
    }
    */

   }