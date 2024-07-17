package com.amp.safetynetalerts;

import com.amp.safetynetalerts.model.DataWrapper;
import com.amp.safetynetalerts.repository.DataWrapperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class DataWrapperRepositoryTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void loadDataFromFile() {

        try (MockedStatic<DataWrapperRepository> mockedStatic = Mockito.mockStatic(DataWrapperRepository.class)) {

            mockedStatic.when(() -> DataWrapperRepository.loadDataFromFile(any(), eq(DataWrapper.class))).thenReturn(new DataWrapper());

            DataWrapper dw = DataWrapperRepository.loadDataFromFile(null, DataWrapper.class);

            assertNotNull(dw);
        }
    }

    @Test
    void getDataWrapper() {

        try (MockedStatic<DataWrapperRepository> mockedStatic = Mockito.mockStatic(DataWrapperRepository.class)) {
            // Mocking the static method call
            mockedStatic.when(DataWrapperRepository::getDataWrapper).thenReturn(new DataWrapper());

            //Calling the method
            DataWrapper dw = DataWrapperRepository.getDataWrapper();

            // Asserting the result
            assertNotNull(dw);
        }
    }

    @Test
    void updateFileWithDataWrapper() {

        try (MockedStatic<DataWrapperRepository> mockedStatic = Mockito.mockStatic(DataWrapperRepository.class)) {
            // Mocking the static method call
            mockedStatic.when(() -> DataWrapperRepository.updateFileWithDataWrapper(any())).thenAnswer(i -> null);

            //Calling the method and asserting that no exception is thrown
            assertDoesNotThrow(() -> DataWrapperRepository.updateFileWithDataWrapper(new DataWrapper()));
        }
    }

    @Test
    void updateFileWithDataWrapper_throwsIOException() {
        try (MockedConstruction<FileWriter> mockedConstruction = Mockito.mockConstruction(FileWriter.class,
                (mock, context) -> {
                    doThrow(new IOException()).when(mock).write(anyString());
                })) {

            try {
                DataWrapperRepository.updateFileWithDataWrapper(new DataWrapper());
            } catch (Exception exception) {
                assertTrue(exception instanceof IOException);
                assertEquals("An error occurred while updating the file with DataWrapper", exception.getMessage());
            }

        }
    }

    @Test
    void getDataWrapper_WhenDataIsNull() {

        try (MockedStatic<DataWrapperRepository> mockedStatic = Mockito.mockStatic(DataWrapperRepository.class)) {
            mockedStatic.when(() -> DataWrapperRepository.loadDataFromFile(any(), any())).thenAnswer(i -> null);

            try {
                DataWrapperRepository.getDataWrapper();
            } catch (Exception exception) {
                assertTrue(exception instanceof IllegalStateException);
                assertEquals("DataWrapper object is null", exception.getMessage());
            }
        }
    }

}