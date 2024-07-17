package com.amp.safetynetalerts.utils;

import com.amp.safetynetalerts.model.MedicalRecord;
import com.amp.safetynetalerts.dto.MedicalRecordDTO;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordMapper {

    public static MedicalRecordDTO toMedicalRecordDTO(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            return null;
        }
        
        MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO();

        medicalRecordDTO.setFirstName(medicalRecord.getFirstName());
        medicalRecordDTO.setLastName(medicalRecord.getLastName());
        medicalRecordDTO.setBirthdate(medicalRecord.getBirthdate());
        // We create new lists for medications and allergies to avoid potential side effects
        // of modifying the original lists
        medicalRecordDTO.setMedications(medicalRecord.getMedications());
        medicalRecordDTO.setAllergies(medicalRecord.getAllergies());

        return medicalRecordDTO;
    }
}