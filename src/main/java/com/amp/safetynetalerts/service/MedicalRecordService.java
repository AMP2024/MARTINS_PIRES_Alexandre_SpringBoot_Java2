package com.amp.safetynetalerts.service;

import com.amp.safetynetalerts.dto.*;
import com.amp.safetynetalerts.model.DataWrapper;
import com.amp.safetynetalerts.model.MedicalRecord;
import com.amp.safetynetalerts.utils.*;
import java.io.IOException;
import java.util.List;

import com.amp.safetynetalerts.repository.DataWrapperRepository;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordService {

    public static MedicalRecord getMedicalRecord(List<MedicalRecord> medicalRecords, String firstName, String lastName) {

        for (MedicalRecord record : medicalRecords) {
            if (record.getFirstName().equals(firstName) && record.getLastName().equals(lastName)) {
                return record;
            }
        }
        return null;
    }

    public void updateMedicalRecord(List<MedicalRecord> medicalRecords, String firstName, String lastName, String newBirthdate, List<String> newMedications, List<String> newAllergies) {

        for (MedicalRecord record : medicalRecords) {
            if (record.getFirstName().equals(firstName) && record.getLastName().equals(lastName)) {
                if (newBirthdate != null) {
                    record.setBirthdate(newBirthdate);
                }
                if (newMedications != null) {
                    record.setMedications(newMedications);
                }
                if (newAllergies != null) {
                    record.setAllergies(newAllergies);
                }
            }
        }
    }

    public void deleteMedicalRecordByName(List<MedicalRecord> medicalRecords, String firstName, String lastName) {

        boolean isRemoved = medicalRecords.removeIf(record -> record.getFirstName().equals(firstName) && record.getLastName().equals(lastName));

        if (!isRemoved) {
            throw new IllegalStateException("No medical record was deleted");
        }
    }

    public MedicalRecordDTO getMedicalRecordDataWrapper(String firstName, String lastName) {
    DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
    MedicalRecord medicalRecord = MedicalRecordService.getMedicalRecord(
            dataWrapper.getMedicalrecords(),
            firstName,
            lastName
    );
    return MedicalRecordMapper.toMedicalRecordDTO(medicalRecord);
}
    public MedicalRecordDTO addMedicalRecordDataWrapper(MedicalRecord medicalRecord) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();

        // Get the List of medical records from the DataWrapper
        List<MedicalRecord> medicalRecords = dataWrapper.getMedicalrecords();

        // Check if the medical record exists.
        for (MedicalRecord record : medicalRecords) {
            if (record.getFirstName().equals(medicalRecord.getFirstName())
                    && record.getLastName().equals(medicalRecord.getLastName())) {
                return null;
            }
        }

        // If it doesn't exist, then add it to the list
        medicalRecords.add(medicalRecord);

        // Update the DataWrapper and save it
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);

        return MedicalRecordMapper.toMedicalRecordDTO(medicalRecord);
    }

    public MedicalRecordDTO updateMedicalRecordDataWrapper(MedicalRecord medicalRecordToUpdate) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        String firstName = medicalRecordToUpdate.getFirstName();
        String lastName = medicalRecordToUpdate.getLastName();
        String birthdate = medicalRecordToUpdate.getBirthdate();
        List<String> medications = medicalRecordToUpdate.getMedications();
        List<String> allergies = medicalRecordToUpdate.getAllergies();

        MedicalRecord existingMedicalRecord = MedicalRecordService.getMedicalRecord(dataWrapper.getMedicalrecords(), firstName, lastName);
        if (existingMedicalRecord == null) {
            return null;
        }
       updateMedicalRecord(
                dataWrapper.getMedicalrecords(),
                firstName,
                lastName,
                birthdate,
                medications,
                allergies
        );
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);

       return MedicalRecordMapper.toMedicalRecordDTO(medicalRecordToUpdate);
    }

    public void deleteMedicalRecordDataWrapper(String firstName, String lastName) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        deleteMedicalRecordByName(dataWrapper.getMedicalrecords(), firstName, lastName);
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);
    }

}