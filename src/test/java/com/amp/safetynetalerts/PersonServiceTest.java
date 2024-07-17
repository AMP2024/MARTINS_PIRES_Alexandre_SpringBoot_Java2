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


import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import java.util.*;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.web.servlet.NoHandlerFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void testAddAndPersistPerson() throws IOException {
        
        PersonDTO result = personServiceMock.addAndPersistPerson(person2);

        assertNotNull(result);
        assertEquals(PersonMapper.toPersonDTO(person2), result);
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

}