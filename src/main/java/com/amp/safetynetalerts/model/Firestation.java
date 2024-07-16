package com.amp.safetynetalerts.model;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Firestation {

    private String address;
    private Integer station;
}
