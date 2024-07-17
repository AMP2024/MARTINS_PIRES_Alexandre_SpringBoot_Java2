package com.amp.safetynetalerts.service;

import com.amp.safetynetalerts.dto.FirestationDTO;
import com.amp.safetynetalerts.dto.PersonWithMedicalRecordDTO;
import com.amp.safetynetalerts.exception.FirestationDeleteException;
import com.amp.safetynetalerts.exception.FirestationUpdateException;
import com.amp.safetynetalerts.model.DataWrapper;
import com.amp.safetynetalerts.model.Firestation;

import com.amp.safetynetalerts.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amp.safetynetalerts.model.MedicalRecord;
import com.amp.safetynetalerts.model.Person;
import com.amp.safetynetalerts.repository.DataWrapperRepository;
import org.springframework.stereotype.Service;

@Service
public class FirestationService {

    public static List<String> getFirestationAddresses(List<Firestation> firestations, Integer stationNumber) {

        List<String> addresses = new ArrayList<>();
        for (Firestation firestation : firestations) {
            if (firestation.getStation().equals(stationNumber)) {
                addresses.add(firestation.getAddress());
            }
        }
        return addresses;
    }

    public static Integer getFirestationNumber(List<Firestation> firestations, String address) {

        for (Firestation firestation : firestations) {
            if (firestation.getAddress().equals(address)) {
                return firestation.getStation();
            }
        }
        return null;
    }

    public void updateFirestationStationNumber(List<Firestation> firestations, String address, Integer newStationNumber) {

        boolean addressExists = false;

        for (Firestation firestation : firestations) {
            if (firestation.getAddress().equals(address)) {
                if (newStationNumber != null) {
                    firestation.setStation(newStationNumber);
                    addressExists = true;
                }
            }
        }

        if (!addressExists) {
            throw new FirestationUpdateException("The address does not exist in firestations");
        }
    }

    public void deleteFirestation(List<Firestation> firestations, String address, Integer stationNumber) {

        if (address == null && stationNumber == null) {
            throw new IllegalArgumentException("Both address and stationNumber cannot be null");
        }

        boolean isAddressDeleted = false;
        boolean isStationNumberDeleted = false;

        if (address != null) {
            isAddressDeleted = firestations.removeIf(firestation -> firestation.getAddress().equals(address));
        }
        if (stationNumber != null) {
            isStationNumberDeleted = firestations.removeIf(firestation -> firestation.getStation().equals(stationNumber));
        }

        if (!isAddressDeleted && !isStationNumberDeleted) {
            throw new FirestationDeleteException("No matching firestations to delete were found.");
        }
    }

    public void addFirestation(List<Firestation> firestations, String address, Integer station) {

        Firestation newFirestation = new Firestation();
        newFirestation.setAddress(address);
        newFirestation.setStation(station);
        firestations.add(newFirestation);
    }

    public static List<String> getFirestationAddressesByStationNumber(List<Firestation> firestations, Integer targetStationNumber) {

        List<String> addresses = new ArrayList<>();
        for (Firestation firestation : firestations) {
            if (targetStationNumber != null && firestation.getStation().equals(targetStationNumber)) {
                addresses.add(firestation.getAddress());
            }
        }
        return addresses;
    }

    public Map<String, Object> getDataByFirestationNumber(Integer stationNumber) {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        List<String> addresses = FirestationService.getFirestationAddressesByStationNumber(dataWrapper.getFirestations(), stationNumber);
        List<Person> persons = PersonService.getPersonsByAddresses(dataWrapper.getPersons(), addresses);
        List<MedicalRecord> medicalRecords = new ArrayList<>();

        for (Person person : persons) {
            MedicalRecord record = MedicalRecordService.getMedicalRecord(dataWrapper.getMedicalrecords(), person.getFirstName(), person.getLastName());
            if (record != null) {
                medicalRecords.add(record);
            }
        }
        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOS = PersonService.processPersonsAndRecords(persons, medicalRecords);

        Map<String, Object> response = new HashMap<>();
        response.put("persons", personWithMedicalRecordDTOS);
        response.put("count", PersonService.countAdultsAndChildren(personWithMedicalRecordDTOS));
        return response;
    }

    public List<String> getAddressesFromFirestation(Integer station) {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        List<String> addresses = FirestationService.getFirestationAddresses(dataWrapper.getFirestations(), station);

        if (addresses.isEmpty()) {
            throw new IllegalArgumentException("No addresses found for the given station number");
        }

        return addresses;
    }

    public FirestationDTO addFirestationDataWrapper(String address, Integer station) throws IOException {

        Firestation firestation = new Firestation();
        firestation.setAddress(address);
        firestation.setStation(station);

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        addFirestation(dataWrapper.getFirestations(), firestation.getAddress(), firestation.getStation());
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);

        return FirestationMapper.toFirestationDTO(firestation);
    }
    public void updateFirestationStationNumberWrapper(String address, Integer newStationNumber) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        updateFirestationStationNumber(dataWrapper.getFirestations(), address, newStationNumber);
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);
    }

    public void deleteFirestationDataWrapper(String address, Integer stationNumber) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        deleteFirestation(
                dataWrapper.getFirestations(),
                address,
                stationNumber);
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);
    }

    public static List<String> getPhoneNumbersByFirestation(Integer firestationNumber) {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        List<String> addresses = FirestationService.getFirestationAddresses(dataWrapper.getFirestations(), firestationNumber);
        List<Person> persons = PersonService.getPersonsByAddresses(dataWrapper.getPersons(), addresses);

        return PersonService.getListOfPhoneNumbers(persons, addresses);
    }

}
