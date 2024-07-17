package com.amp.safetynetalerts.controller;

import com.amp.safetynetalerts.dto.*;
import com.amp.safetynetalerts.model.*;
import com.amp.safetynetalerts.service.PersonService;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class SafetynetalertsController {

    private static final Logger logger = LogManager.getLogger(SafetynetalertsController.class);
    private final FirestationService firestationService;
    private final PersonService personService;
    private final MedicalRecordService medicalRecordService;

    public SafetynetalertsController(FirestationService firestationService,
                                     PersonService personService,
                                     MedicalRecordService medicalRecordService) {
        this.firestationService = firestationService;
        this.personService = personService;
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/person")
    public ResponseEntity<?> getPerson(@RequestParam String firstName, @RequestParam String lastName) throws Exception {

        logger.info("Retrieving person with firstname: {}, lastname: {}", firstName, lastName);

        PersonDTO person = personService.fetchPerson(firstName, lastName);

        logger.debug("Retrieved person data: {}", person);

        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @PostMapping(path = "/person", consumes = "application/json")
    public ResponseEntity<?> addPerson(@RequestBody Person person) throws Exception {

        PersonDTO savedPerson = null;

        logger.info("Adding person: {}", person);

        savedPerson = personService.addAndPersistPerson(person);

        logger.info("Person successfully added: {}", savedPerson);

        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);

    }

    @PutMapping(path = "/person", consumes = "application/json")
    public ResponseEntity<?> updatePerson(@RequestBody Person person) throws Exception {

        logger.info("Updating the person : {}", person);

        personService.updatePersonDataWrapper(person);

        logger.debug("Person successfully updated : {}", person);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("/person")
    public ResponseEntity<String> deletePerson(@RequestParam String firstName, @RequestParam String lastName) throws Exception {

        logger.info("Deleting person with first name: {}, last name: {}", firstName, lastName);

        personService.deletePersonDataWrapper(firstName, lastName);

        logger.debug("Person successfully deleted.");

        return new ResponseEntity<>("Person successfully deleted.", HttpStatus.NO_CONTENT);

    }

}
