package com.amp.safetynetalerts.repository;

import com.amp.safetynetalerts.model.DataWrapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Repository;

/**
 * Repository class that handles data read/write operations to a JSON file.
 */
@Repository
public class DataWrapperRepository {

    private static final Logger LOGGER = LogManager.getLogger(DataWrapperRepository.class);

    /**
     * Reads data stored in a JSON file and maps it into an object.
     *
     * @param type     The type of the object to which the data should be mapped.
     * @param classOfT The class type to which the data should be mapped.
     * @return The object mapped from the JSON file.
     */
    public static <T> T loadDataFromFile(T type, Class<T> classOfT) {
        T data = null;
        try (JsonReader reader = new JsonReader(new FileReader("data.json"))) {
            Gson gson = new Gson();
            data = gson.fromJson(reader, classOfT);
        } catch (IOException e) {
            LOGGER.error("An error occurred while reading the data.json file", e);
        }
        return data;
    }

    /**
     * Retrieves the DataWrapper object from the JSON file.
     *
     * @return The DataWrapper object.
     * @throws IllegalStateException If the DataWrapper object is null.
     */
    public static DataWrapper getDataWrapper() {
        DataWrapper data = loadDataFromFile(null, DataWrapper.class);
        if (data == null) {
            LOGGER.error("DataWrapper object is null");
            throw new IllegalStateException("DataWrapper object is null");
        }
        return data;
    }

    /**
     * Updates the data.json file with the provided DataWrapper object.
     *
     * @param dataWrapper The DataWrapper object to be saved into the file.
     * @throws IOException If an error occurred while updating the file with DataWrapper.
     */
    public static void updateFileWithDataWrapper(DataWrapper dataWrapper) throws IOException {
        try (Writer writer = new FileWriter("data.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(dataWrapper, writer);
        } catch (IOException e) {
            LOGGER.error("An error occurred while updating the data.json file with DataWrapper", e);
            throw new IOException("An error occurred while updating the file with DataWrapper", e);
        }
    }
}