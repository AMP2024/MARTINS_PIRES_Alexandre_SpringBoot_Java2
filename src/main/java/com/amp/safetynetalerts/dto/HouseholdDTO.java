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
public class HouseholdDTO {

    private List<HouseholdMemberDTO> householdMembers;
}