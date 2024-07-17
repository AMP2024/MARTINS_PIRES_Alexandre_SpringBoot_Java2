package com.amp.safetynetalerts.controller;

import com.amp.safetynetalerts.dto.*;
import com.amp.safetynetalerts.exception.*;
import com.amp.safetynetalerts.model.*;
import com.amp.safetynetalerts.service.FirestationService;
import com.amp.safetynetalerts.service.MedicalRecordService;
import com.amp.safetynetalerts.service.PersonService;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

import org.springframework.lang.Nullable;

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

    @GetMapping("/addressesFromFirestationNumber")
    public ResponseEntity<?> getAddressesFromFirestationNumber(@RequestParam Integer station) {

        logger.info("Retrieving addresses for station number : {}", station);

        List<String> addresses = firestationService.getAddressesFromFirestation(station);

        logger.debug("Addresses retrieved : {}", addresses);

        return new ResponseEntity<>(addresses, HttpStatus.OK);


    }

    @PostMapping("/firestation")
    public ResponseEntity<FirestationDTO> addFirestation(@RequestParam String address, @RequestParam Integer station) throws Exception {


        logger.info("Adding a station with the address : {}, station : {}", address, station);

        FirestationDTO firestation = firestationService.addFirestationDataWrapper(address, station);

        logger.debug("Station successfully added");

        return new ResponseEntity<>(firestation, HttpStatus.CREATED);

    }

    @PutMapping("/firestation")
    public ResponseEntity<String> updateFirestation(@RequestParam String address, @RequestParam Integer newStationNumber) throws Exception {

        logger.info("Updating the station with the address: {}, new station number: {}", address, newStationNumber);

        firestationService.updateFirestationStationNumberWrapper(address, newStationNumber);

        logger.debug("Station successfully updated");

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("/firestation")
    public ResponseEntity<?> deleteFirestation(@Nullable @RequestParam(required = false) String address,
                                               @Nullable @RequestParam(required = false) Integer stationNumber) throws Exception {

        logger.info("Deleting the station with the address: {}, station number: {}", address, stationNumber);

        firestationService.deleteFirestationDataWrapper(address, stationNumber);

        logger.debug("Station successfully deleted");

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping("/medicalRecord")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {

        logger.info("Retrieving medical record with first name: {}, last name: {}", firstName, lastName);

        MedicalRecordDTO medicalRecord = medicalRecordService.getMedicalRecordDataWrapper(firstName, lastName);

        if (medicalRecord == null) {

            throw new MedicalRecordNotFoundException("Medical Record for " + firstName + " " + lastName + " not found.");
        }

        logger.debug("Medical record retrieved: {}", medicalRecord);

        return new ResponseEntity<>(medicalRecord, HttpStatus.OK);
    }

    @PostMapping("/medicalRecord")
    public ResponseEntity<MedicalRecordDTO> addMedicalRecord(@RequestBody MedicalRecord medicalRecord) throws Exception {

        MedicalRecordDTO resultingMedicalRecord = null;

        logger.info("Adding a new medical record: {}", medicalRecord);

        resultingMedicalRecord = medicalRecordService.addMedicalRecordDataWrapper(medicalRecord);

        logger.info("Medical record successfully added: {}", resultingMedicalRecord);

        return new ResponseEntity<>(resultingMedicalRecord, HttpStatus.CREATED);
    }

    @PutMapping("/medicalRecord")
    public ResponseEntity<?> updateMedicalRecord(@RequestBody MedicalRecord medicalRecordToUpdate) throws Exception {

        logger.info("Updating medical record: {}", medicalRecordToUpdate);

        MedicalRecordDTO updatedRecord = medicalRecordService.updateMedicalRecordDataWrapper(medicalRecordToUpdate);

        if (updatedRecord == null) {
            throw new MedicalRecordNotFoundException("Medical Record not found for provided name");
        }

        logger.info("Medical record successfully updated: {}", updatedRecord);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/medicalRecord")
    public ResponseEntity<?> deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) throws IOException {

        logger.info("Deleting medical record for person with first name: {}, last name: {}", firstName, lastName);

        medicalRecordService.deleteMedicalRecordDataWrapper(firstName, lastName);

        logger.info("Medical record successfully deleted for person with first name: {}, last name: {}", firstName, lastName);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/personsByFirestationNumber")
    public ResponseEntity<Map<String, Object>> getPersonsByFirestationNumber(@RequestParam Integer stationNumber) {

        Map<String, Object> response = null;

        logger.info("Retrieving persons' information by firestation number: {}", stationNumber);

        response = firestationService.getDataByFirestationNumber(stationNumber);

        if (response == null) {
            throw new FirestationNotFoundException("Firestation with number " + stationNumber + " not found");
        }

        logger.debug("Data retrieved by firestation number: {}", response);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/childAlert")
    public ResponseEntity<?> getChildAlert(@RequestParam String address) {

        logger.info("Retrieving child alert information for the address: {}", address);

        Optional<Map<String, HouseholdDTO>> childAlertData = personService.getChildAlertData(address);

        if (childAlertData.isEmpty()) {
            throw new PersonNotFoundException("No child found at provided address: " + address);
        }

        logger.info("Success in retrieving child alert information for the address: {}", address);

        return new ResponseEntity<>(childAlertData.get(), HttpStatus.OK);
    }

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneAlert(@RequestParam(value = "firestation") Integer firestationNumber){

        List<String> phoneNumbers;

        logger.info("Retrieving phone numbers for firestation number: {}", firestationNumber);

        phoneNumbers = firestationService.getPhoneNumbersByFirestation(firestationNumber);

        logger.info("Successfully retrieved phone numbers for firestation number: {}", firestationNumber);

        return new ResponseEntity<>(phoneNumbers, HttpStatus.OK);
    }

    @GetMapping("/fire")
    public ResponseEntity<List<DataOfInhabitantsDTO>> getFireInfoByAddress(@RequestParam String address) {

        List<DataOfInhabitantsDTO> dataOfInhabitantsDTOS;

        logger.info("Retrieving fire information by address: {}", address);

        dataOfInhabitantsDTOS = personService.processDataByAddress(address);

        logger.info("Fire information successfully retrieved by address: {}", address);

        return new ResponseEntity<>(dataOfInhabitantsDTOS, HttpStatus.OK);
    }

    @GetMapping("/flood/stations")
    public ResponseEntity<Map<Integer, List<DataOfInhabitantsDTO>>> getPersonsByListOfFirestationNumber(@RequestParam(value = "stations") List<Integer> stationNumbers)  {

        Map<Integer, List<DataOfInhabitantsDTO>> response;

        logger.info("Retrieving persons by list of firestation numbers: {}", stationNumbers);

        response = personService.getPersonsDataByFirestationNumber(stationNumbers);

        logger.info("Successfully retrieved persons by list of firestation numbers");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/personInfo")
    public ResponseEntity<List<PersoInfoDTO>> getPersonInfo(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {

        logger.info("Retrieving person information with first name: {}, and last name: {}", firstName, lastName);

        List<PersoInfoDTO> result = personService.getPersonInfoDataWrapper(firstName, lastName);

        logger.debug("Person's information has been retrieved: {}", result);

        logger.info("Successfully retrieved person information for first name: {}, and last name: {}", firstName, lastName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmails(@RequestParam String city){

        logger.info("Retrieving community emails for the city: {}", city);

        List<String> emails = personService.getCommunityEmailsService(city);

        logger.debug("Community emails retrieved: {}", emails);

        logger.info("Successfully retrieved community emails for the city: {}", city);

        return ResponseEntity.ok(emails);
    }
}

