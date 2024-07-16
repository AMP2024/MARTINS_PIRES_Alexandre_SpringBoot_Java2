package com.amp.safetynetalerts.dto;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberDTO {

    private String firstName;
    private String lastName;
    private int age;
}