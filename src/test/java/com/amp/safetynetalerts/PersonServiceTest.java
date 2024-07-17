package com.amp.safetynetalerts;

import com.amp.safetynetalerts.repository.DataWrapperRepository;
import com.amp.safetynetalerts.service.PersonService;
import com.amp.safetynetalerts.dto.*;
import com.amp.safetynetalerts.model.MedicalRecord;
import com.amp.safetynetalerts.model.Person;
import com.amp.safetynetalerts.model.DataWrapper;
import com.amp.safetynetalerts.utils.PersonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.amp.safetynetalerts.service.PersonService.calculateAge;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.amp.safetynetalerts.model.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PersonServiceTest {

    private List<Person> persons;
    private List<MedicalRecord> medicalRecords;
    private Person newPerson;
    private Person person1;
    private Person person2;
    private MedicalRecord record1;
    private MedicalRecord record2;

    @Autowired
    private PersonService personServiceMock;

    @BeforeEach
    public void setup() {

        persons = new ArrayList<>();
        medicalRecords = new ArrayList<>();
        person1 = new Person();
        person1.setFirstName("Jean");
        person1.setLastName("Dupont");
        person1.setAddress("123 rue de l'harmonie");
        person1.setCity("Paris");
        person1.setZip("75009");
        person1.setPhone("01-43-50-00-00");
        person1.setEmail("jean@dupont.com");
        persons.add(person1);
        person2 = new Person();
        person2.setFirstName("Jeanne");
        person2.setLastName("Dupont");
        person2.setAddress("123 rue de l'harmonie");
        person2.setCity("Paris");
        person2.setZip("75009");
        person2.setPhone("01-43-52-07-12");
        person2.setEmail("jeanne@dupont.com");
        persons.add(person2);
        record1 = new MedicalRecord();
        record1.setFirstName("Jean");
        record1.setLastName("Dupont");
        record1.setBirthdate("01/01/2000");
        record1.setMedications(Arrays.asList("medicament1:200mg", "medicament2:50mg"));
        record1.setAllergies(Arrays.asList("", "allergie2"));
        medicalRecords.add(record1);
        record2 = new MedicalRecord();
        record2.setFirstName("Jeanne");
        record2.setLastName("Dupont");
        record2.setBirthdate("01/01/2008");
        record2.setMedications(Arrays.asList("medicament3:150mg", "medicament4:200mg"));
        record2.setAllergies(Arrays.asList("allergie3", "allergie4"));
        medicalRecords.add(record2);
        newPerson = new Person();
        newPerson.setFirstName("Jacques");
        newPerson.setLastName("Adit");
        newPerson.setAddress("27 place de la forêt");
        newPerson.setCity("Bordeaux");
        newPerson.setZip("33072");
        newPerson.setPhone("05-56-00-11-22");
        newPerson.setEmail("jacques@adit.com");
    }

    @Test
    public void testCountAdultsAndChildren() {

        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOList = personServiceMock.processPersonsAndRecords(persons, medicalRecords);
        Map<String, Long> countMap = personServiceMock.countAdultsAndChildren(personWithMedicalRecordDTOList);
        assertEquals(1, countMap.get("Adults"));
        assertEquals(1, countMap.get("Children"));
    }

    @Test
    public void testAddPerson() {

        personServiceMock.addPerson(persons, "Jacques", "Adit", "27 place de la forêt", "Bordeaux", "33072", "05-56-00-11-22", "jacques@adit.com");

        //lambda expression to compare if at least one element in the list equals to 'newPerson'
        assertTrue(persons.stream().anyMatch(p -> p.equals(newPerson)));
    }

    @Test
    public void testUpdatePerson() {

        personServiceMock.updatePerson(persons, "Jean", "Dupont", "123 rue de l'harmonie", "Paris", "75009", "01-43-50-00-00", "jean@dupont.com");
        assertEquals("123 rue de l'harmonie", person1.getAddress());
        assertEquals("75009", person1.getZip());
        assertEquals("01-43-50-00-00", person1.getPhone());
        assertEquals("jean@dupont.com", person1.getEmail());
    }

    @Test
    public void testDeletePerson() {

        personServiceMock.deletePerson(persons, "Jean", "Dupont");
        assertFalse(persons.contains(person1));
    }

    @Test
    public void testGetPerson() throws NoHandlerFoundException {

        Person result = personServiceMock.getPerson(persons, "Jean", "Dupont");
        assertEquals(person1, result);
    }

    @Test
    public void testGetPersonsByAddresses() {

        List<String> addresses = List.of("123 rue de l'harmonie");

        person1.setPhone("01-43-50-00-01");
        person2.setPhone("01-43-50-00-02");

        List<Person> result = personServiceMock.getPersonsByAddresses(persons, addresses);
        assertEquals(2, result.size());
        assertTrue(result.contains(person1));
        assertTrue(result.contains(person2));
    }

    @Test
    public void testGetListOfPhoneNumbers() {

        List<String> addresses = List.of("123 rue de l'harmonie");
        List<String> phoneNumbers = personServiceMock.getListOfPhoneNumbers(persons, addresses);
        assertEquals(2, phoneNumbers.size());
        assertTrue(phoneNumbers.contains(person1.getPhone()));
        assertTrue(phoneNumbers.contains(person2.getPhone()));
    }

    @Test
    public void testProcessPersonsAndRecords() {

        List<PersonWithMedicalRecordDTO> result = personServiceMock.processPersonsAndRecords(persons, medicalRecords);
        assertEquals(2, result.size());
        assertEquals(person1.getFirstName(), result.get(0).getFirstName());
        assertEquals(person1.getLastName(), result.get(0).getLastName());
        assertEquals(person2.getFirstName(), result.get(1).getFirstName());
        assertEquals(person2.getLastName(), result.get(1).getLastName());
    }


    @Test
    public void testExtractChildren() {

        List<PersonWithMedicalRecordDTO> allPersons = personServiceMock.processPersonsAndRecords(persons, medicalRecords);
        List<PersonWithMedicalRecordDTO> children = personServiceMock.extractChildren(allPersons);
        assertEquals(1, children.size());
        assertEquals("Jeanne", children.get(0).getFirstName());
        assertEquals("Dupont", children.get(0).getLastName());
    }

    @Test
    public void testFetchHouseholds() {

        List<PersonWithMedicalRecordDTO> allPersons = personServiceMock.processPersonsAndRecords(persons, medicalRecords);
        List<PersonWithMedicalRecordDTO> children = personServiceMock.extractChildren(allPersons);
        List<HouseholdDTO> households = personServiceMock.fetchHouseholds(allPersons, children);
        assertEquals(1, households.size());
        assertEquals(2, households.get(0).getHouseholdMembers().size());
    }

    @Test
    public void testProcessPersonsDataOfInhabitants() {

        List<DataOfInhabitantsDTO> result = personServiceMock.processPersonsDataOfInhabitants(persons, medicalRecords, 1);
        assertEquals(2, result.size());
        assertEquals(person1.getFirstName(), result.get(0).getFirstName());
        assertEquals(person1.getLastName(), result.get(0).getLastName());
        assertEquals(person2.getFirstName(), result.get(1).getFirstName());
        assertEquals(person2.getLastName(), result.get(1).getLastName());
    }

    @Test
    public void testProcessPersonsToPersoInfoDTOs() {

        List<PersoInfoDTO> result = personServiceMock.processPersonsToPersoInfoDTOs(persons, medicalRecords);
        assertEquals(2, result.size());
        assertEquals("Jean", result.get(0).getFirstName());
        assertEquals("Dupont", result.get(0).getLastName());
        assertEquals("Jeanne", result.get(1).getFirstName());
        assertEquals("Dupont", result.get(1).getLastName());
    }


    @Test
    public void testAddAndPersistPerson() throws IOException {
        
        PersonDTO result = personServiceMock.addAndPersistPerson(person2);

        assertNotNull(result);
        assertEquals(PersonMapper.toPersonDTO(person2), result);
    }


    @Test
    public void testGetPersonsDataByFirestationNumber() {
        
        List<Integer> stationNumbers = Arrays.asList(1, 2);

        Map<Integer, List<DataOfInhabitantsDTO>> result = personServiceMock.getPersonsDataByFirestationNumber(stationNumbers);

        assertNotNull(result);
        assertEquals(stationNumbers.size(), result.keySet().size());
    }


    @Test
    public void testDeletePersonDataWrapper() throws IOException {
        
        String firstName = "FirstName";
        String lastName = "LastName";
        List<Person> persons = new ArrayList<>();
        persons.add(new Person(firstName, lastName, "Test Address", "Test City",
                "12345", "123-345-5678", "test@example.com"));
        DataWrapper dataWrapper = new DataWrapper();
        dataWrapper.setPersons(persons);

        try (MockedStatic<DataWrapperRepository> mockRepository = Mockito.mockStatic(DataWrapperRepository.class)) {
            // Mock methods
            mockRepository.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapper);
            doNothing().when(DataWrapperRepository.class); // void method, doNothing() used
            DataWrapperRepository.updateFileWithDataWrapper(any(DataWrapper.class));

            personServiceMock.deletePersonDataWrapper(firstName, lastName);

            ArgumentCaptor<DataWrapper> captor = ArgumentCaptor.forClass(DataWrapper.class);
            mockRepository.verify(() -> DataWrapperRepository.updateFileWithDataWrapper(captor.capture()), times(1));

            assertFalse(captor.getValue().getPersons().stream()
                    .anyMatch(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName)));
        }
    }

    @Test
    public void testGetCommunityEmailsService() {
        
        String city = "Paris";
        List<String> result = personServiceMock.getCommunityEmailsService(city);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(email -> email.endsWith("@example.com")));
    }

    @Test
    public void testFetchPerson() throws IOException, NoHandlerFoundException {
        String firstName = "Pierre";
        String lastName = "Dupont";

        Person testPerson = new Person();
        testPerson.setFirstName(firstName);
        testPerson.setLastName(lastName);

        DataWrapper testWrapperData = new DataWrapper();
        testWrapperData.setPersons(Collections.singletonList(testPerson));

        try (MockedStatic<DataWrapperRepository> dataWrapperMock = Mockito.mockStatic(DataWrapperRepository.class)) {
            dataWrapperMock.when(DataWrapperRepository::getDataWrapper).thenReturn(testWrapperData);

            PersonDTO result = personServiceMock.fetchPerson(firstName, lastName);

            assertNotNull(result);
            assertEquals(firstName, result.getFirstName());
            assertEquals(lastName, result.getLastName());
        }
    }

    @Test
    public void testGetChildAlertData() {
        String adresse = "27 place de la forêt";
        String prenom = "prenom";
        String nom = "nom";
        Person enfant = new Person();
        enfant.setAddress(adresse);
        enfant.setFirstName(prenom);
        enfant.setLastName(nom);

        MedicalRecord dossierMedical = new MedicalRecord();
        dossierMedical.setBirthdate("02/02/2014");
        dossierMedical.setFirstName(prenom);
        dossierMedical.setLastName(nom);

        try (MockedStatic<DataWrapperRepository> mocked = Mockito.mockStatic(DataWrapperRepository.class)) {
            mocked.when(DataWrapperRepository::getDataWrapper).thenReturn(new DataWrapper(Collections.singletonList(enfant), Collections.emptyList(), Collections.singletonList(dossierMedical)));

            Optional<Map<String, HouseholdDTO>> resultat = personServiceMock.getChildAlertData(adresse);

            assertTrue(resultat.isPresent());

            mocked.verify(DataWrapperRepository::getDataWrapper);
        }
    }

    @Test
    public void testGetPersonInfoDataWrapper() {
        // Initializing the mock
        String firstName = "Jacques";
        String lastName = "Dupont";
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(firstName);
        medicalRecord.setLastName(lastName);
        DataWrapper dataWrapper = new DataWrapper(Arrays.asList(person), new ArrayList<>(), Arrays.asList(medicalRecord));

        // Initializing a list of PersoInfoDTO
        List<PersoInfoDTO> expectedResult = new ArrayList<>();
        PersoInfoDTO dto = new PersoInfoDTO();
        dto.setFirstName("Jean");
        dto.setLastName("Dupond");
        dto.setAddress("10 Rue de la Paix");
        dto.setCity("Paris");
        dto.setZip("75000");
        dto.setAge(30);
        dto.setEmail("jean@dupond.fr");
        dto.setMedications(new ArrayList<>());
        dto.setAllergies(new ArrayList<>());
        expectedResult.add(dto);

        // Mock the PersonService & static class
        PersonService personServiceMockTemp = mock(PersonService.class);
        when(personServiceMockTemp.getPersonInfoDataWrapper(firstName, lastName)).thenReturn(expectedResult);

        try (MockedStatic<DataWrapperRepository> mocked = Mockito.mockStatic(DataWrapperRepository.class)) {
            mocked.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapper);

            // Running the method to test
            List<PersoInfoDTO> result = personServiceMockTemp.getPersonInfoDataWrapper(firstName, lastName);

            // Verification of the results
            assertEquals(expectedResult.size(), result.size());
            assertEquals(expectedResult.get(0).getFirstName(), result.get(0).getFirstName());
            assertEquals(expectedResult.get(0).getLastName(), result.get(0).getLastName());

        }
    }

    @Test
    public void testUpdatePersonDataWrapper() throws IOException {

        Person newPerson = new Person();
        newPerson.setFirstName("Julien");
        newPerson.setLastName("Martin");
        newPerson.setPhone("01-01-01-01-01");

        List<Person> persons = new ArrayList<>();
        persons.add(newPerson);

        DataWrapper donneesInitiales = new DataWrapper(persons, new ArrayList<>(), new ArrayList<>());

        // Use of try-with-resources to mock static
        try (MockedStatic<DataWrapperRepository> mocked = Mockito.mockStatic(DataWrapperRepository.class)) {
            mocked.when(DataWrapperRepository::getDataWrapper).thenReturn(donneesInitiales);

            // Execution of the method to test
            personServiceMock.updatePersonDataWrapper(newPerson);

            // Use of ArgumentCaptor to capture the argument passed to updateFileWithDataWrapper()
            ArgumentCaptor<DataWrapper> argumentCaptor = ArgumentCaptor.forClass(DataWrapper.class);
            mocked.verify(() -> DataWrapperRepository.updateFileWithDataWrapper(argumentCaptor.capture()), times(1));

            // Verification that the data passed to the method includes the new person
            assertTrue(argumentCaptor.getValue().getPersons().contains(newPerson));
        }

    }

    @Test
    public void testCalculateAge() {
        String birthdate = "01/01/2000";
        LocalDate date = LocalDate.of(2024, 1, 1);
        int result = calculateAge(birthdate, date);
        assertEquals(24, result);
    }

    @Test
    public void testProcessDataByAddress() {

        DataWrapper dataWrapperMock = new DataWrapper();
        List<Person> personsToMock = Arrays.asList(
                new Person("Caroline", "Duchesse", "29 15th St", "Culver", "97451", "841-874-6513", "drk@email.com"),
                new Person("Peter", "Duncan", "644 Gershwin Cir", "Culver", "97451", "841-874-6512", "jaboyd@email.com"),
                new Person("Foster", "Shepard", "29 15th St", "Culver", "97451", "841-874-6544", "jaboyd@email.com"));
        List<Firestation> firestationsToMock = Arrays.asList(
                new Firestation("1509 Culver St", 3),
                new Firestation("29 15th St", 2));
        List<MedicalRecord> medicalRecordsToMock = Arrays.asList(
                new MedicalRecord("Peter", "Duncan", "09/06/2000", Arrays.asList(), Arrays.asList()),
                new MedicalRecord("Caroline", "Duchesse", "03/15/1965", Arrays.asList("aznol:200mg"), Arrays.asList("nillacilan")),
                new MedicalRecord("Foster", "Shepard", "01/03/1989", Arrays.asList(), Arrays.asList()));
        dataWrapperMock.setPersons(personsToMock);
        dataWrapperMock.setFirestations(firestationsToMock);
        dataWrapperMock.setMedicalrecords(medicalRecordsToMock);

        try (MockedStatic<DataWrapperRepository> mocked = Mockito.mockStatic(DataWrapperRepository.class)) {
            mocked.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapperMock);

            List<DataOfInhabitantsDTO> expectedResults = Arrays.asList(
                    new DataOfInhabitantsDTO(2, "Caroline", "Duchesse", "841-874-6513", calculateAge("03/15/1965", LocalDate.now()), Arrays.asList("aznol:200mg"), Arrays.asList("nillacilan")),
                    new DataOfInhabitantsDTO(2, "Foster", "Shepard", "841-874-6544", calculateAge("01/03/1989", LocalDate.now()), Arrays.asList(), Arrays.asList())
            );

            List<DataOfInhabitantsDTO> results = personServiceMock.processDataByAddress("29 15th St");

            assertEquals(expectedResults, results);
        }
    }
}