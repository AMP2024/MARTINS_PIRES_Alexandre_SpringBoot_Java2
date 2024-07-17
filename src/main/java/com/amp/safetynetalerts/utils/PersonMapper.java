package com.amp.safetynetalerts.utils;

import com.amp.safetynetalerts.model.Person;
import com.amp.safetynetalerts.dto.PersonDTO;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {
    
    public static PersonDTO toPersonDTO(Person person) {
        if (person == null) {
            return null;
        }

        PersonDTO personDTO = new PersonDTO();

        personDTO.setFirstName(person.getFirstName());
        personDTO.setLastName(person.getLastName());
        personDTO.setAddress(person.getAddress());
        personDTO.setCity(person.getCity());
        personDTO.setZip(person.getZip());
        personDTO.setPhone(person.getPhone());
        personDTO.setEmail(person.getEmail());

        return personDTO;
    }
}