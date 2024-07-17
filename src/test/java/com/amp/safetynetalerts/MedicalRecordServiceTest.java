package com.amp.safetynetalerts;

import com.amp.safetynetalerts.dto.MedicalRecordDTO;
import com.amp.safetynetalerts.model.DataWrapper;
import com.amp.safetynetalerts.model.MedicalRecord;
import com.amp.safetynetalerts.repository.DataWrapperRepository;
import com.amp.safetynetalerts.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MedicalRecordServiceTest {
    private MedicalRecordService medicalRecordService;
    private List<MedicalRecord> mockMedicalRecords;

    @MockBean
    private DataWrapperRepository dataWrapperRepository;

    @BeforeEach
    public void setup() {

        medicalRecordService = new MedicalRecordService();
        mockMedicalRecords = new ArrayList<>();

        MedicalRecord record1 = new MedicalRecord();
        record1.setFirstName("John");
        record1.setLastName("Doe");
        record1.setBirthdate("01/01/2000");
        mockMedicalRecords.add(record1);

        MedicalRecord record2 = new MedicalRecord();
        record2.setFirstName("Jane");
        record2.setLastName("Doe");
        record2.setBirthdate("02/02/2002");
        mockMedicalRecords.add(record2);
    }

    @Test
    public void getMedicalRecordTest() {

        MedicalRecord expected = mockMedicalRecords.get(0);
        MedicalRecord actual = MedicalRecordService.getMedicalRecord(mockMedicalRecords, "John", "Doe");
        assertEquals(expected, actual, "Records should match");
    }

    @Test
    public void updateMedicalRecordTest() {

        String newBirthdate = "01/01/2001";
        medicalRecordService.updateMedicalRecord(mockMedicalRecords, "John", "Doe", newBirthdate, null, null);
        String updatedBirthdate = MedicalRecordService.getMedicalRecord(mockMedicalRecords, "John", "Doe").getBirthdate();
        assertEquals(newBirthdate, updatedBirthdate);
    }

    @Test
    public void deleteMedicalRecordByNameTest() {

        medicalRecordService.deleteMedicalRecordByName(mockMedicalRecords, "John", "Doe");
        MedicalRecord deletedRecord = MedicalRecordService.getMedicalRecord(mockMedicalRecords, "John", "Doe");
        assertNull(deletedRecord);
    }

    @Test
    public void getMedicalRecordDataWrapperTest() {
        try (MockedStatic<MedicalRecordService> recordServiceMock = Mockito.mockStatic(MedicalRecordService.class);
             MockedStatic<DataWrapperRepository> dataWrapperRepositoryMock = Mockito.mockStatic(DataWrapperRepository.class)) {
            DataWrapper dataWrapper = new DataWrapper();
            dataWrapper.setMedicalrecords(new ArrayList<>());
            MedicalRecord record = new MedicalRecord("John", "Doe", "01/01/2000", null, null);
            dataWrapper.setMedicalrecords(Collections.singletonList(record));
            dataWrapperRepositoryMock.when(DataWrapperRepository::getDataWrapper).thenReturn(dataWrapper);
            recordServiceMock.when(() -> MedicalRecordService.getMedicalRecord(anyList(), anyString(), anyString())).thenReturn(record);

            MedicalRecordDTO result = medicalRecordService.getMedicalRecordDataWrapper("John", "Doe");
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertEquals("01/01/2000", result.getBirthdate());
        }
    }

    @Test
    public void addMedicalRecordDataWrapperTest()  throws IOException {
        try (MockedStatic<DataWrapperRepository> dataWrapperRepositoryMock = Mockito.mockStatic(DataWrapperRepository.class)) {
            DataWrapper dataWrapper = new DataWrapper();
            dataWrapper.setMedicalrecords(new ArrayList<>());
            MedicalRecord record = new MedicalRecord("John", "Doe", "01/01/2000", null, null);
            dataWrapper.setMedicalrecords(new ArrayList<>());
            when(dataWrapperRepository.getDataWrapper()).thenReturn(dataWrapper);
            dataWrapperRepositoryMock.when(() -> DataWrapperRepository.updateFileWithDataWrapper(any(DataWrapper.class))).thenAnswer(i -> i.getArguments()[0]);

            MedicalRecordDTO result = medicalRecordService.addMedicalRecordDataWrapper(record);
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertEquals("01/01/2000", result.getBirthdate());
        }
    }

    @Test
    public void updateMedicalRecordDataWrapperTest()  throws IOException {
        try (MockedStatic<DataWrapperRepository> dataWrapperRepositoryMock = Mockito.mockStatic(DataWrapperRepository.class);
             MockedStatic<MedicalRecordService> recordServiceMock = Mockito.mockStatic(MedicalRecordService.class)) {
            DataWrapper dataWrapper = new DataWrapper();
            dataWrapper.setMedicalrecords(new ArrayList<>());
            MedicalRecord record = new MedicalRecord("John", "Doe", "01/01/2000", null, null);
            dataWrapper.setMedicalrecords(Collections.singletonList(record));
            when(dataWrapperRepository.getDataWrapper()).thenReturn(dataWrapper);
            dataWrapperRepositoryMock.when(() -> DataWrapperRepository.updateFileWithDataWrapper(any(DataWrapper.class))).thenAnswer(i -> i.getArguments()[0]);
            recordServiceMock.when(() -> MedicalRecordService.getMedicalRecord(anyList(), anyString(), anyString())).thenReturn(record);

            MedicalRecord newRecord = new MedicalRecord("John", "Doe", "01/01/2001", null, null);
            MedicalRecordDTO result = medicalRecordService.updateMedicalRecordDataWrapper(newRecord);
            assertEquals("01/01/2001", result.getBirthdate());
        }
    }
    @Test
    public void deleteMedicalRecordDataWrapperTest() throws IOException  {
        try (MockedStatic<DataWrapperRepository> dataWrapperRepositoryMock = Mockito.mockStatic(DataWrapperRepository.class)) {
            DataWrapper dataWrapper = new DataWrapper();
            MedicalRecord record = new MedicalRecord("John", "Doe", "01/01/2000", null, null);
            dataWrapper.setMedicalrecords(new ArrayList<>(Arrays.asList(record)));
            when(dataWrapperRepository.getDataWrapper()).thenReturn(dataWrapper);
            dataWrapperRepositoryMock.when(() -> DataWrapperRepository.updateFileWithDataWrapper(any(DataWrapper.class))).thenAnswer(i -> i.getArguments()[0]);

            medicalRecordService.deleteMedicalRecordDataWrapper("John", "Doe");
            assertTrue(dataWrapper.getMedicalrecords().isEmpty());
        }
    }
}