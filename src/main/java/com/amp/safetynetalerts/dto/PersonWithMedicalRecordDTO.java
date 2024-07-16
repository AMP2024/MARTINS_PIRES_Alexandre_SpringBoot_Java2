package com.amp.safetynetalerts.dto;

import java.util.List;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonWithMedicalRecordDTO {

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;
    private int age;
    private String birthdate;
    private List<String> medications;
    private List<String> allergies;
}
