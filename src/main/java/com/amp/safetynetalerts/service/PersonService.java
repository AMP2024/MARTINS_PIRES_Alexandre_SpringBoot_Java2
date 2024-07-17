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


    /**
     * Counts the number of adults and children in a list of PersonWithMedicalRecordDTO objects.
     *
     * @param persons a list of PersonWithMedicalRecordDTO objects
     * @return a map containing the count of adults and children, where the keys are "Adults" and "Children"
     */
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

    /**
     * Adds a new person to the given list of persons.
     *
     * @param persons the list of persons to add the new person to
     * @param firstName the first name of the new person (can be null)
     * @param lastName the last name of the new person (can be null)
     * @param address the address of the new person (can be null)
     * @param city the city of the new person (can be null)
     * @param zip the zip code of the new person (can be null)
     * @param phone the phone number of the new person (can be null)
     * @param email the email address of the new person (can be null)
     */
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

    /**
     * Updates the information of a person in the given list of persons.
     *
     * @param persons   The list of persons to update from.
     * @param firstName The first name of the person.
     * @param lastName  The last name of the person.
     * @param address   The new address of the person. Can be null to not update.
     * @param city      The new city of the person. Can be null to not update.
     * @param zip       The new zip code of the person. Can be null to not update.
     * @param phone     The new phone number of the person. Can be null to not update.
     * @param email     The new email address of the person. Can be null to not update.
     * @throws PersonUpdateException if the person is not found in the list.
     */
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

    /**
     * Deletes a person from the provided list based on the first name and last name.
     * Throws a PersonDeleteException if no person is found with the provided details.
     *
     * @param persons    the list of persons from which the person needs to be deleted
     * @param firstName  the first name of the person to be deleted
     * @param lastName   the last name of the person to be deleted
     * @throws PersonDeleteException if no person is found with the provided first name and last name
     */
    public void deletePerson(List<Person> persons, String firstName, String lastName) {

        boolean isDeleted = persons.removeIf(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName));

        if (!isDeleted) {
            throw new PersonDeleteException("No person was deleted with the provided user details!");
        }
    }

    /**
     * Get the person with the specified first name and last name from a list of persons.
     *
     * @param persons    a list of Person objects to search from
     * @param firstName  the first name of the person to find
     * @param lastName   the last name of the person to find
     * @return the Person object with the specified first name and last name
     * @throws NoHandlerFoundException if no person with the specified first name and last name is found
     */
    public static Person getPerson(List<Person> persons, String firstName, String lastName) throws NoHandlerFoundException {

        for (Person person : persons) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                return person;
            }
        }
        throw new NoHandlerFoundException("GET", "/" + firstName + "/" + lastName, null);
    }

    /**
     * Retrieves a list of Person objects based on a list of addresses.
     *
     * @param persons   The list of Person objects to filter.
     * @param addresses The list of addresses to filter the persons by.
     * @return A list of Person objects matching the provided addresses.
     */
    public static List<Person> getPersonsByAddresses(List<Person> persons, List<String> addresses) {

        List<Person> result = new ArrayList<>();
        for (Person person : persons) {
            if (addresses.contains(person.getAddress())) {
                result.add(person);
            }
        }
        return result;
    }

    /**
     * Calculates the age based on the birthdate and the current date.
     *
     * @param birthdate the birthdate of the person in the format "MM/dd/yyyy"
     * @param date the current date
     * @return the age of the person
     */
    public static int calculateAge(String birthdate, LocalDate date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthDate = LocalDate.parse(birthdate, formatter);

        return Period.between(birthDate, date).getYears();
    }

    /**
     * Processes a list of Person objects and a list of MedicalRecord objects to create a new list of PersonWithMedicalRecordDTO objects.
     * Each PersonWithMedicalRecordDTO object contains the information from a Person object matched with the corresponding MedicalRecord object based on first name and last name.
     * The age of each PersonWithMedicalRecordDTO object is calculated based on the birthdate in the MedicalRecord object and the current date.
     *
     * @param persons        the list of Person objects to process
     * @param medicalRecords the list of MedicalRecord objects to process
     * @return a new list of PersonWithMedicalRecordDTO objects containing the combined information from the Person and MedicalRecord objects
     */
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

    /**
     * Processes a list of Person objects and a list of MedicalRecord objects to create a new list of DataOfInhabitantsDTO objects.
     * Each DataOfInhabitantsDTO object contains the combined information from a Person object and the corresponding MedicalRecord object, based on their first name and last name
     * .
     *
     * @param persons The list of Person objects to process.
     * @param medicalRecords The list of MedicalRecord objects to process.
     * @param firestationNumber The fire station number to set in each DataOfInhabitantsDTO object.
     * @return A new list of DataOfInhabitantsDTO objects containing the combined information from the Person and MedicalRecord objects.
     */
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

    /**
     * Extracts children from a list of PersonWithMedicalRecordDTO objects
     *
     * @param persons the list of PersonWithMedicalRecordDTO objects
     * @return the list of PersonWithMedicalRecordDTO objects representing children
     */
    public List<PersonWithMedicalRecordDTO> extractChildren(List<PersonWithMedicalRecordDTO> persons) {

        List<PersonWithMedicalRecordDTO> children = new ArrayList<>();
        for (PersonWithMedicalRecordDTO person : persons) {
            if (person.getAge() >= 0 && person.getAge() <= 17) {
                children.add(person);
            }
        }
        return children;
    }

    /**
     * Fetches households based on the given list of persons and children.
     *
     * @param persons  the list of persons with medical records
     * @param children the list of children with medical records
     * @return the list of households as HouseholdDTO objects
     */
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

    /**
     * Returns a list of unique phone numbers from the list of persons, filtered by a list of addresses.
     *
     * @param persons    A list of Person objects to filter.
     * @param addresses  A list of addresses to use as filter criteria.
     * @return A list of unique phone numbers found in the persons list and matching the addresses list.
     */
    public static List<String> getListOfPhoneNumbers(List<Person> persons, List<String> addresses) {

        List<String> phoneNumbers = new ArrayList<>();
        for (Person person : persons) {
            if (addresses.contains(person.getAddress()) && !phoneNumbers.contains(person.getPhone())) {
                phoneNumbers.add(person.getPhone());
            }
        }
        return phoneNumbers;
    }

    /**
     * Processes a list of Person objects and a list of MedicalRecord objects to create a new list of PersoInfoDTO objects.
     * Each PersoInfoDTO object contains information from a Person object matched with the corresponding MedicalRecord object based on first name and last name.
     *
     * @param persons        the list of Person objects to process
     * @param medicalRecords the list of MedicalRecord objects to process
     * @return a new list of PersoInfoDTO objects containing the combined information from the Person and MedicalRecord objects
     */
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

    /**
     * Fetches a PersonDTO object based on the given first name and last name.
     *
     * @param firstName the first name of the person to fetch
     * @param lastName  the last name of the person to fetch
     * @return the fetched PersonDTO object
     * @throws NoHandlerFoundException if no person with the specified first name and last name is found
     */
    public static PersonDTO fetchPerson(String firstName, String lastName) throws NoHandlerFoundException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        return PersonMapper.toPersonDTO(getPerson(dataWrapper.getPersons(), firstName, lastName));
    }

    /**
     * Adds a person to the data wrapper and persists the changes,
     * then returns a PersonDTO object representing the added person.
     *
     * @param person the person object to be added and persisted
     * @return a PersonDTO object representing the added person
     * @throws IOException if any I/O error occurs during the persistence
     */
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

    /**
     * Retrieves child alert data for the given address.
     *
     * @param address the address to retrieve child alert data for
     * @return an Optional containing a Map of HouseholdDTO objects, where the key is the number of household members
     *         followed by the last name of the first household member, and the value is the corresponding HouseholdDTO object.
     *         Returns an empty Optional if no child alert data is found.
     */
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
    /**
     * Processes data for inhabitants based on the given address.
     *
     * @param address the address of the inhabitants
     * @return a list of DataOfInhabitantsDTO containing the processed data for the inhabitants
     */
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

    /**
     * Retrieves the data of persons living in the specified fire station numbers.
     *
     * @param stationNumbers The list of fire station numbers
     * @return A map containing the data of inhabitants for each fire station number. The key is the fire station number and the value is the list of DataOfInhabitantsDTO objects
     * .
     */
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
    /**
     * Updates the person data in the data wrapper and persists the changes in the data wrapper file.
     *
     * @param person The person object containing the updated data.
     * @throws IOException If an I/O error occurs while updating the data wrapper file.
     */
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

    /**
     * Deletes the person data with the given first name and last name from the data wrapper.
     *
     * @param firstName the first name of the person
     * @param lastName the last name of the person
     * @throws IOException if there is an error updating the data wrapper file
     */
    public void deletePersonDataWrapper(String firstName, String lastName) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        deletePerson(dataWrapper.getPersons(), firstName, lastName);
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);

    }

    /**
     * Retrieves a list of PersonInfoDTO objects based on the given first name and last name.
     *
     * @param firstName The first name of the persons to filter.
     * @param lastName The last name of the persons to filter.
     * @return A list of PersonInfoDTO objects representing the filtered persons' information.
     */
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

    /**
     * Retrieves a list of unique email addresses for persons residing in the given city.
     *
     * @param city the city to filter persons by
     * @return a list of unique email addresses for persons residing in the given city
     */
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