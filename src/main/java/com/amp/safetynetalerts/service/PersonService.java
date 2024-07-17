package com.amp.safetynetalerts.service;

import com.amp.safetynetalerts.dto.*;
import com.amp.safetynetalerts.exception.PersonDeleteException;
import com.amp.safetynetalerts.exception.PersonUpdateException;
import com.amp.safetynetalerts.model.DataWrapper;
import com.amp.safetynetalerts.model.MedicalRecord;
import com.amp.safetynetalerts.model.Person;
import com.amp.safetynetalerts.utils.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.amp.safetynetalerts.repository.DataWrapperRepository;
import lombok.Data;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.amp.safetynetalerts.service.FirestationService.getFirestationAddressesByStationNumber;
import static com.amp.safetynetalerts.service.MedicalRecordService.getMedicalRecord;

@Data
@Service
public class PersonService {


    public static Map<String, Long> countAdultsAndChildren(List<PersonWithMedicalRecordDTO> persons) {

        Map<String, Long> countMap = new HashMap<>();

        long adultCount = persons.stream()
                .filter(person -> person.getAge() >= 18)
                .count();
        long childCount = persons.stream()
                .filter(person -> person.getAge() < 18)
                .count();

        countMap.put("Adults", adultCount);
        countMap.put("Children", childCount);

        return countMap;
    }

    public void addPerson(List<Person> persons, String firstName, String lastName, String address, String city, String zip, String phone, String email) {

        Person person = new Person();
        if (firstName != null) {
            person.setFirstName(firstName);
        }
        if (lastName != null) {
            person.setLastName(lastName);
        }
        if (address != null) {
            person.setAddress(address);
        }
        if (city != null) {
            person.setCity(city);
        }
        if (zip != null) {
            person.setZip(zip);
        }
        if (phone != null) {
            person.setPhone(phone);
        }
        if (email != null) {
            person.setEmail(email);
        }
        persons.add(person);
    }

    public void updatePerson(List<Person> persons, String firstName, String lastName, String address, String city, String zip, String phone, String email) {

        boolean personFound = false;

        for (Person person : persons) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                personFound = true;
                if (address != null) {
                    person.setAddress(address);
                }
                if (city != null) {
                    person.setCity(city);
                }
                if (zip != null) {
                    person.setZip(zip);
                }
                if (phone != null) {
                    person.setPhone(phone);
                }
                if (email != null) {
                    person.setEmail(email);
                }
                break;
            }
        }

        if (!personFound) {
            throw new PersonUpdateException("Person not found");
        }
    }

    public void deletePerson(List<Person> persons, String firstName, String lastName) {

        boolean isDeleted = persons.removeIf(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName));

        if (!isDeleted) {
            throw new PersonDeleteException("No person was deleted with the provided user details!");
        }
    }

    public static Person getPerson(List<Person> persons, String firstName, String lastName) throws NoHandlerFoundException {

        for (Person person : persons) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                return person;
            }
        }
        throw new NoHandlerFoundException("GET", "/" + firstName + "/" + lastName, null);
    }

    public static List<Person> getPersonsByAddresses(List<Person> persons, List<String> addresses) {

        List<Person> result = new ArrayList<>();
        for (Person person : persons) {
            if (addresses.contains(person.getAddress())) {
                result.add(person);
            }
        }
        return result;
    }

    public static int calculateAge(String birthdate, LocalDate date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthDate = LocalDate.parse(birthdate, formatter);

        return Period.between(birthDate, date).getYears();
    }

    public static List<PersonWithMedicalRecordDTO> processPersonsAndRecords(List<Person> persons, List<MedicalRecord> medicalRecords) {

        List<PersonWithMedicalRecordDTO> result = new ArrayList<>();
        for (Person person : persons) {
            for (MedicalRecord record : medicalRecords) {
                if (person.getFirstName().equals(record.getFirstName()) && person.getLastName().equals(record.getLastName())) {
                    PersonWithMedicalRecordDTO dto = new PersonWithMedicalRecordDTO();
                    dto.setFirstName(person.getFirstName());
                    dto.setLastName(person.getLastName());
                    dto.setAddress(person.getAddress());
                    dto.setCity(person.getCity());
                    dto.setZip(person.getZip());
                    dto.setPhone(person.getPhone());
                    dto.setEmail(person.getEmail());
                    dto.setAge(calculateAge(record.getBirthdate(), LocalDate.now()));
                    dto.setBirthdate(record.getBirthdate());
                    dto.setMedications(record.getMedications());
                    dto.setAllergies(record.getAllergies());
                    result.add(dto);
                }
            }
        }
        return result;
    }

    public static List<DataOfInhabitantsDTO> processPersonsDataOfInhabitants(List<Person> persons, List<MedicalRecord> medicalRecords, Integer firestationNumber) {

        List<DataOfInhabitantsDTO> result = new ArrayList<>();
        for (Person person : persons) {
            for (MedicalRecord record : medicalRecords) {
                if (person.getFirstName().equals(record.getFirstName()) && person.getLastName().equals(record.getLastName())) {
                    DataOfInhabitantsDTO dto = new DataOfInhabitantsDTO();
                    dto.setFirestationNumber(firestationNumber);
                    dto.setFirstName(person.getFirstName());
                    dto.setLastName(person.getLastName());
                    dto.setPhone(person.getPhone());
                    dto.setAge(calculateAge(record.getBirthdate(),LocalDate.now()));
                    dto.setMedications(record.getMedications());
                    dto.setAllergies(record.getAllergies());
                    result.add(dto);
                }
            }
        }
        return result;
    }

    public List<PersonWithMedicalRecordDTO> extractChildren(List<PersonWithMedicalRecordDTO> persons) {

        List<PersonWithMedicalRecordDTO> children = new ArrayList<>();
        for (PersonWithMedicalRecordDTO person : persons) {
            if (person.getAge() >= 0 && person.getAge() <= 17) {
                children.add(person);
            }
        }
        return children;
    }

    public List<HouseholdDTO> fetchHouseholds(List<PersonWithMedicalRecordDTO> persons, List<PersonWithMedicalRecordDTO> children) {

        List<HouseholdDTO> households = new ArrayList<>();
        for (PersonWithMedicalRecordDTO child : children) {
            HouseholdDTO household = new HouseholdDTO();
            List<HouseholdMemberDTO> childrenMembers = new ArrayList<>();
            List<HouseholdMemberDTO> adultMembers = new ArrayList<>();
            for (PersonWithMedicalRecordDTO person : persons) {
                if (person.getLastName().equals(child.getLastName()) && person.getAddress().equals(child.getAddress())) {
                    HouseholdMemberDTO member = new HouseholdMemberDTO();
                    member.setFirstName(person.getFirstName());
                    member.setLastName(person.getLastName());
                    member.setAge(person.getAge());
                    if (person.getAge() < 18) {
                        childrenMembers.add(member);
                    } else {
                        adultMembers.add(member);
                    }
                }
            }
            childrenMembers.sort(Comparator.comparingInt(HouseholdMemberDTO::getAge));   // sort children from youngest to oldest
            adultMembers.sort(Comparator.comparingInt(HouseholdMemberDTO::getAge));      // sort adults from youngest to oldest
            childrenMembers.addAll(adultMembers);
            household.setHouseholdMembers(childrenMembers);
            if (!households.contains(household)) {
                households.add(household);
            }
        }
        return households;
    }

    public static List<String> getListOfPhoneNumbers(List<Person> persons, List<String> addresses) {

        List<String> phoneNumbers = new ArrayList<>();
        for (Person person : persons) {
            if (addresses.contains(person.getAddress()) && !phoneNumbers.contains(person.getPhone())) {
                phoneNumbers.add(person.getPhone());
            }
        }
        return phoneNumbers;
    }

    public List<PersoInfoDTO> processPersonsToPersoInfoDTOs(List<Person> persons, List<MedicalRecord> medicalRecords) {

        List<PersoInfoDTO> result = new ArrayList<>();
        for (Person person : persons) {
            for (MedicalRecord record : medicalRecords) {
                if (person.getFirstName().equals(record.getFirstName()) && person.getLastName().equals(record.getLastName())) {
                    PersoInfoDTO dto = new PersoInfoDTO();
                    dto.setFirstName(person.getFirstName());
                    dto.setLastName(person.getLastName());
                    dto.setAddress(person.getAddress());
                    dto.setCity(person.getCity());
                    dto.setZip(person.getZip());
                    dto.setEmail(person.getEmail());
                    dto.setAge(calculateAge(record.getBirthdate(),LocalDate.now()));
                    dto.setMedications(record.getMedications());
                    dto.setAllergies(record.getAllergies());
                    result.add(dto);
                }
            }
        }
        return result;
    }

    public static PersonDTO fetchPerson(String firstName, String lastName) throws NoHandlerFoundException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        return PersonMapper.toPersonDTO(getPerson(dataWrapper.getPersons(), firstName, lastName));
    }

    public PersonDTO addAndPersistPerson(Person person) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        addPerson(
                dataWrapper.getPersons(),
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getCity(),
                person.getZip(),
                person.getPhone(),
                person.getEmail()
        );
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);
        return PersonMapper.toPersonDTO(person);
    }

    public Optional<Map<String, HouseholdDTO>> getChildAlertData(String address) {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        List<Person> persons = getPersonsByAddresses(dataWrapper.getPersons(), Collections.singletonList(address));

        List<MedicalRecord> medicalRecords = new ArrayList<>();
        for (Person person : persons) {
            MedicalRecord record = MedicalRecordService.getMedicalRecord(dataWrapper.getMedicalrecords(), person.getFirstName(), person.getLastName());
            if (record != null) {
                medicalRecords.add(record);
            }
        }
        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOS = processPersonsAndRecords(persons, medicalRecords);

        List<PersonWithMedicalRecordDTO> children = extractChildren(personWithMedicalRecordDTOS);
        if (children.isEmpty()) {
            return Optional.empty();
        }

        List<HouseholdDTO> households = fetchHouseholds(personWithMedicalRecordDTOS, children);
        Map<String, HouseholdDTO> response = new HashMap<>();
        for (HouseholdDTO household : households) {
            String key = household.getHouseholdMembers().size() + " " + household.getHouseholdMembers().get(0).getLastName();
            response.put(key, household);
        }
        return Optional.of(response);
    }
    public List<DataOfInhabitantsDTO> processDataByAddress(String address) {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        Integer firestationNumber = FirestationService.getFirestationNumber(dataWrapper.getFirestations(), address);
        List<Person> persons = PersonService.getPersonsByAddresses(dataWrapper.getPersons(), Collections.singletonList(address));
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        for (Person person : persons) {
            MedicalRecord record = MedicalRecordService.getMedicalRecord(dataWrapper.getMedicalrecords(), person.getFirstName(), person.getLastName());
            if (record != null) {
                medicalRecords.add(record);
            }
        }
        return PersonService.processPersonsDataOfInhabitants(persons, medicalRecords, firestationNumber);
    }

    public Map<Integer, List<DataOfInhabitantsDTO>> getPersonsDataByFirestationNumber(List<Integer> stationNumbers) {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        Map<Integer, List<DataOfInhabitantsDTO>> response = new HashMap<>();

        for (Integer stationNumber : stationNumbers) {
            List<String> addresses = getFirestationAddressesByStationNumber(dataWrapper.getFirestations(), stationNumber);
            List<Person> persons = getPersonsByAddresses(dataWrapper.getPersons(), addresses);

            List<MedicalRecord> medicalRecords = new ArrayList<>();
            for (Person person : persons) {
                MedicalRecord record = getMedicalRecord(dataWrapper.getMedicalrecords(), person.getFirstName(), person.getLastName());
                if (record != null) {
                    medicalRecords.add(record);
                }
            }

            List<DataOfInhabitantsDTO> inhabitants = processPersonsDataOfInhabitants(persons, medicalRecords, stationNumber);
            response.put(stationNumber, inhabitants);
        }

        return response;
    }
    public void updatePersonDataWrapper(Person person) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        updatePerson(dataWrapper.getPersons(),
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getCity(),
                person.getZip(),
                person.getPhone(),
                person.getEmail());
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);
    }

    public void deletePersonDataWrapper(String firstName, String lastName) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        deletePerson(dataWrapper.getPersons(), firstName, lastName);
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);

    }

    public List<PersoInfoDTO> getPersonInfoDataWrapper(String firstName, String lastName) {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        List<Person> persons = dataWrapper.getPersons();
        List<MedicalRecord> medicalRecords = dataWrapper.getMedicalrecords();

        // Filter the persons by firstName and lastName
        List<Person> personsFiltered = persons.stream()
                .filter(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName))
                .collect(Collectors.toList());

        // Call processPersonsToPersoInfoDTOs method in the PersonService to get the list of PersoInfoDTO
        return processPersonsToPersoInfoDTOs(personsFiltered, medicalRecords);
    }

    public List<String> getCommunityEmailsService(String city) {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();

        // Filter persons by city and remove duplicates by email using distinct
        List<Person> persons = dataWrapper.getPersons().stream()
                .filter(person -> person.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());

        // Extract emails and check for uniqueness
        List<String> emails = persons.stream()
                .map(Person::getEmail)
                .distinct()
                .collect(Collectors.toList());

        return emails;
    }

}