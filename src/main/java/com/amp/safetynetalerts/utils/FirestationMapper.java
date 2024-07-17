package com.amp.safetynetalerts.utils;

import com.amp.safetynetalerts.model.Firestation;
import com.amp.safetynetalerts.dto.FirestationDTO;
import org.springframework.stereotype.Component;

@Component
public class FirestationMapper {

    public static FirestationDTO toFirestationDTO(Firestation firestation) {
        if (firestation == null) {
            return null;
        }
        
        FirestationDTO firestationDTO = new FirestationDTO();

        firestationDTO.setAddress(firestation.getAddress());
        firestationDTO.setStation(firestation.getStation());

        return firestationDTO;
    }
}