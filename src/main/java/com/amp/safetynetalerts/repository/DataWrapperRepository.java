package com.amp.safetynetalerts.repository;

import com.amp.safetynetalerts.model.DataWrapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Repository;

@Repository
public class DataWrapperRepository {

    private static final Logger LOGGER = LogManager.getLogger(DataWrapperRepository.class);

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

    public static DataWrapper getDataWrapper() {
        DataWrapper data = loadDataFromFile(null, DataWrapper.class);
        if (data == null) {
            LOGGER.error("DataWrapper object is null");
            throw new IllegalStateException("DataWrapper object is null");
        }
        return data;
    }

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
