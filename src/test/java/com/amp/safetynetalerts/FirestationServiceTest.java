package com.amp.safetynetalerts;

import com.amp.safetynetalerts.dto.FirestationDTO;
import com.amp.safetynetalerts.exception.FirestationUpdateException;
import com.amp.safetynetalerts.repository.DataWrapperRepository;
import com.amp.safetynetalerts.model.Firestation;
import com.amp.safetynetalerts.service.FirestationService;
import com.amp.safetynetalerts.model.Person;
import com.amp.safetynetalerts.model.DataWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;

// Mockito methods
import static org.mockito.Mockito.times;

import com.amp.safetynetalerts.service.PersonService;

import org.mockito.Mock;
import org.mockito.Mockito;

// java.util
import java.util.Arrays;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FirestationServiceTest {

    @MockBean
    private FirestationService firestationService;

    private List<Firestation> mockFirestations;

    @MockBean
    private DataWrapper dataWrapper;

    @Mock
    private DataWrapper dataWrapperMock;

    @BeforeEach
    public void setUp() {
        dataWrapperMock.setPersons(null);

        mockFirestations = new ArrayList<>();
        dataWrapper = new DataWrapper();
        dataWrapper.setFirestations(mockFirestations);

        firestationService = new FirestationService();

        Firestation station1 = new Firestation();
        station1.setAddress("Address1");
        station1.setStation(1);
        mockFirestations.add(station1);

        Firestation station2 = new Firestation();
        station2.setAddress("Address2");
        station2.setStation(2);
        mockFirestations.add(station2);

        Firestation station3 = new Firestation();
        station3.setAddress("Address3");
        station3.setStation(1);
        mockFirestations.add(station3);
    }

    @Test
    public void getFirestationAddressesTest() {

        List<String> expected = new ArrayList<>();
        expected.add("Address1");
        expected.add("Address3");

        List<String> actual = FirestationService.getFirestationAddresses(mockFirestations, 1);
        assertEquals(expected, actual, "Addresses should match");
    }

    @Test
    public void getFirestationNumberTest() {

        Integer expected = 2;
        Integer actual = FirestationService.getFirestationNumber(mockFirestations, "Address2");
        assertEquals(expected, actual, "Station numbers should match");
    }

    @Test
    public void updateFirestationStationNumberTest() {

        firestationService.updateFirestationStationNumber(mockFirestations, "Address1", 3);
        Integer updatedStationNumber = FirestationService.getFirestationNumber(mockFirestations, "Address1");
        assertEquals(3, updatedStationNumber);
    }

    @Test
    public void testUpdateFirestationStationNumber_Throws_Exception() {
        String testAddress_NonExistant = "testAddress_NonExistant";
        Integer testNewFirestationNumber = 4;

        Throwable exception = assertThrows(FirestationUpdateException.class, () ->
                firestationService.updateFirestationStationNumber(mockFirestations, testAddress_NonExistant, testNewFirestationNumber));
        assertEquals("The address does not exist in firestations", exception.getMessage());
    }

    @Test
    public void deleteFirestationTest() {

        firestationService.deleteFirestation(mockFirestations, "Address1", null);
        Integer deletedStationNumber = FirestationService.getFirestationNumber(mockFirestations, "Address1");
        assertNull(deletedStationNumber);
    }

    @Test
    public void addFirestationTest() {

        firestationService.addFirestation(mockFirestations, "New Address", 4);
        Integer newStationNumber = FirestationService.getFirestationNumber(mockFirestations, "New Address");
        assertEquals(4, newStationNumber);
    }

    @Test
    public void getFirestationAddressesByStationNumberTest() {

        List<String> expected = new ArrayList<>();
        expected.add("Address1");
        expected.add("Address3");

        List<String> actual = FirestationService.getFirestationAddressesByStationNumber(mockFirestations, 1);
        assertEquals(expected, actual, "Addresses should match");
    }

    @Test
    void getDataByFirestationNumberTest() {
        
        int stationNumber = 1;

        List<Person> persons = new ArrayList<>();
        dataWrapper.setPersons(persons);

        try (MockedStatic<DataWrapperRepository> mockedService = mockStatic(DataWrapperRepository.class)) {
            mockedService.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapper);

            
            Map<String, Object> result = firestationService.getDataByFirestationNumber(stationNumber);

            
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(2);
            mockedService.verify(DataWrapperRepository::getDataWrapper, times(1));
        }
    }

    @Test
    void getAddressesFromFirestationTest() {
        
        List<String> expectedAddresses = Arrays.asList("Address1", "Address3");
        try (MockedStatic<DataWrapperRepository> mockedService = mockStatic(DataWrapperRepository.class)) {
            mockedService.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapper);
            
            List<String> result = firestationService.getAddressesFromFirestation(1);

            assertThat(result.size()).isEqualTo(expectedAddresses.size());
            assertThat(result).isEqualTo(expectedAddresses);
            mockedService.verify(DataWrapperRepository::getDataWrapper, times(1));
        }
    }

    @Test
    void addFirestationDataWrapperTest()  throws IOException {
        
        String address = "Address";
        Integer station = 1;
        try (MockedStatic<DataWrapperRepository> mockedService = mockStatic(DataWrapperRepository.class)) {
            mockedService.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapper);
            mockedService.when(() -> DataWrapperRepository.updateFileWithDataWrapper(any(DataWrapper.class))).thenAnswer(i -> null);
            
            FirestationDTO result = firestationService.addFirestationDataWrapper(address, station);

            assertThat(result).isNotNull();
            assertThat(result.getAddress()).isEqualTo(address);
            assertThat(result.getStation()).isEqualTo(station);
            mockedService.verify(DataWrapperRepository::getDataWrapper, times(1));
            mockedService.verify(() -> DataWrapperRepository.updateFileWithDataWrapper(any(DataWrapper.class)), times(1));
        }
    }

    @Test
    void deleteFirestationDataWrapperTest() throws IOException  {
        
        String address = "Address";
        Integer station = 1;
        try (MockedStatic<DataWrapperRepository> mockedService = mockStatic(DataWrapperRepository.class)) {
            mockedService.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapper);
            mockedService.when(() -> DataWrapperRepository.updateFileWithDataWrapper(any(DataWrapper.class))).thenAnswer(i -> null);

            
            firestationService.deleteFirestationDataWrapper(address, station);

            
            mockedService.verify(DataWrapperRepository::getDataWrapper, times(1));
            mockedService.verify(() -> DataWrapperRepository.updateFileWithDataWrapper(any(DataWrapper.class)), times(1));
        }


    }

    @Test
    public void getPhoneNumbersByFirestationTest() {

        try (MockedStatic<DataWrapperRepository> dataWrapperRepoMock = Mockito.mockStatic(DataWrapperRepository.class);
             MockedStatic<FirestationService> firestationServiceMock = Mockito.mockStatic(FirestationService.class);
             MockedStatic<PersonService> personServiceMock = Mockito.mockStatic(PersonService.class)) {

            dataWrapperRepoMock.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapper);

            firestationServiceMock.when(() -> FirestationService.getFirestationAddresses(Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());

            personServiceMock.when(() -> PersonService.getPersonsByAddresses(Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());
            personServiceMock.when(() -> PersonService.getListOfPhoneNumbers(Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());

            Integer firestationNumber = 1;

            List<String> result = FirestationService.getPhoneNumbersByFirestation(firestationNumber);

            assertEquals(Collections.emptyList(), result);

        }
    }
    @Test
    public void testUpdateFirestationStationNumberWrapper1() throws IOException {

        String testAddress = mockFirestations.get(1).getAddress();
        Integer testNewFirestationNumber = 4;

        // Mock static methods using Mockito.mockStatic().
        try (MockedStatic<DataWrapperRepository> utilities = Mockito.mockStatic(DataWrapperRepository.class)) {
            utilities.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapper);
            utilities.when(() -> DataWrapperRepository.updateFileWithDataWrapper(dataWrapper)).thenAnswer(invocation -> null);

            // Create a spy of the service class so we can listen its method calls.
            FirestationService serviceSpy = Mockito.spy(FirestationService.class);

            // Call the method to be tested.
            serviceSpy.updateFirestationStationNumberWrapper(testAddress, testNewFirestationNumber);

            // Validation: check if updateFirestationStationNumber method is correctly called with expected parameters.
            Mockito.verify(serviceSpy, Mockito.times(1)).updateFirestationStationNumber(dataWrapper.getFirestations(), testAddress, testNewFirestationNumber);

            // Validate that the new number is correctly updated.
            assertEquals(testNewFirestationNumber, dataWrapper.getFirestations().get(1).getStation());
        }
    }

    @Test
    public void testUpdateFirestationStationNumberWrapper_Throws_Exception() {

        String testAddress = "Address that does not exist";
        Integer testNewFirestationNumber = 4;

        // Mock static methods using Mockito.mockStatic().
        try (MockedStatic<DataWrapperRepository> utilities = Mockito.mockStatic(DataWrapperRepository.class)) {
            utilities.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapper);
            utilities.when(() -> DataWrapperRepository.updateFileWithDataWrapper(dataWrapper)).thenAnswer(invocation -> null);

            // Create a spy of the Service class so we can listen to its method calls.
            FirestationService serviceSpy = Mockito.spy(FirestationService.class);

            // Expect an exception to be thrown here.
            Exception exception = assertThrows(FirestationUpdateException.class, () -> {
                // Call the method under test.
                serviceSpy.updateFirestationStationNumberWrapper(testAddress, testNewFirestationNumber);
            });

            // Validate that the error message in exception is as expected.
            assertEquals("The address does not exist in firestations", exception.getMessage());

            // Validation: check if updateFirestationStationNumber was indeed called with expected parameters
            Mockito.verify(serviceSpy, Mockito.times(1)).updateFirestationStationNumber(dataWrapper.getFirestations(), testAddress, testNewFirestationNumber);
        }
    }

}