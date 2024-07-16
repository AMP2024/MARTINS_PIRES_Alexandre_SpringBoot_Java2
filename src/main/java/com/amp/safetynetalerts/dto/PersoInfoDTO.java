package com.amp.safetynetalerts.dto;

import lombok.Data;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersoInfoDTO {

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private int age;
    private String email;
    private List<String> medications;
    private List<String> allergies;
}

