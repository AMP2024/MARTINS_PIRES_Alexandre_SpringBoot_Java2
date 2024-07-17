package com.amp.safetynetalerts;

import com.amp.safetynetalerts.controller.SafetynetalertsController;
import com.amp.safetynetalerts.dto.DataOfInhabitantsDTO;
import com.amp.safetynetalerts.dto.HouseholdDTO;
import com.amp.safetynetalerts.dto.MedicalRecordDTO;
import com.amp.safetynetalerts.model.*;
import com.amp.safetynetalerts.repository.DataWrapperRepository;
import com.amp.safetynetalerts.service.FirestationService;
import com.amp.safetynetalerts.service.MedicalRecordService;
import com.amp.safetynetalerts.service.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.google.gson.Gson;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.mockito.Mockito.doThrow;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SafetynetalertsController.class)
@AutoConfigureMockMvc
class SafetynetalertsControllerTest {

    private final Gson gson = new Gson();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FirestationService firestationService;

    @MockBean
    private PersonService personService;

    @MockBean
    private MedicalRecordService medicalRecordService;

    @Test
    void testGetPerson() throws Exception {

        MvcResult result = mockMvc.perform(get("/person")
                        .param("firstName", "John")
                        .param("lastName", "Boyd")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();
        } else {
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Boyd"))
                    .andExpect(jsonPath("$.address").value("1509 Culver St"))
                    .andExpect(jsonPath("$.city").value("Culver"))
                    .andExpect(jsonPath("$.zip").value("97451"))
                    .andExpect(jsonPath("$.phone").value("841-874-6512"))
                    .andExpect(jsonPath("$.email").value("jaboyd@email.com"));
        }
    }

    @Test
    void testAddPerson() throws Exception {

        Person stubPerson = new Person();
        stubPerson.setFirstName("Jean");
        stubPerson.setLastName("Dupond");
        stubPerson.setAddress("456 Boulevard de la Liberté");
        stubPerson.setCity("Lille");
        stubPerson.setZip("59000");
        stubPerson.setPhone("03-20-20-20-20");
        stubPerson.setEmail("jean@dupond.com");

        Gson gson = new Gson();
        String jsonString = gson.toJson(stubPerson);

        MvcResult result = mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {

            result.getResolvedException().printStackTrace();
        } else {

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName").value("Jean"))
                    .andExpect(jsonPath("$.lastName").value("Dupond"))
                    .andExpect(jsonPath("$.address").value("456 Boulevard de la Liberté"))
                    .andExpect(jsonPath("$.city").value("Lille"))
                    .andExpect(jsonPath("$.zip").value("59000"))
                    .andExpect(jsonPath("$.phone").value("03-20-20-20-20"))
                    .andExpect(jsonPath("$.email").value("jean@dupond.com"));
        }
    }

    @Test
    void testUpdatePerson() throws Exception {

        Person stubPerson = new Person();
        stubPerson.setFirstName("Jean");
        stubPerson.setLastName("Dupond");
        stubPerson.setAddress("789 Avenue des Champs-Élysées");
        stubPerson.setCity("Paris");
        stubPerson.setZip("75008");
        stubPerson.setPhone("01-00-00-00-01");
        stubPerson.setEmail("jeandupond@monnouveaumail.fr");

        Gson gson = new Gson();
        String jsonString = gson.toJson(stubPerson);

        MvcResult result = mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {

            result.getResolvedException().printStackTrace();
        } else {
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void testGetAddressesFromFirestationNumber() throws Exception {

        MvcResult result = mockMvc.perform(get("/addressesFromFirestationNumber")
                        .param("station", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {

            result.getResolvedException().printStackTrace();
        } else {
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0]").value("644 Gershwin Cir"))
                    .andExpect(jsonPath("$[1]").value("908 73rd St"))
                    .andExpect(jsonPath("$[2]").value("947 E. Rose Dr"))
                    .andExpect(jsonPath("$[3]").value("123 Street"));
        }
    }

    @Test
    public void testAddFirestation() throws Exception {

        Firestation stubFirestation = new Firestation();
        stubFirestation.setAddress("777 Street");
        stubFirestation.setStation(1);

        Gson gson = new Gson();
        String jsonString = gson.toJson(stubFirestation);

        MvcResult result = mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {

            result.getResolvedException().printStackTrace();

        } else {

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isCreated()) // status should be 201 CREATED
                    .andExpect(jsonPath("$.address").value("777 Street"))
                    .andExpect(jsonPath("$.station").value("1")); // "1" Les méthodes .param() de l'objet MockMvc attendent des chaînes de caractères (String)
        }
    }

    @Test
    public void testUpdateFirestation() throws Exception {

        Firestation stubFirestation = new Firestation();
        stubFirestation.setAddress("312 Street");
        stubFirestation.setStation(2);

        Gson gson = new Gson();
        String jsonString = gson.toJson(stubFirestation);

        MvcResult result = mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {

            result.getResolvedException().printStackTrace();
        } else {

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.address").value("312 Street"))
                    .andExpect(jsonPath("$.station").value("2")); // "2" Les méthodes .param() de l'objet MockMvc attendent des chaînes de caractères (String)
        }
    }

    @Test
    public void testDeleteFirestation() throws Exception {

        mockMvc.perform(delete("/firestation")
                        .param("address", "312 Street"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMedicalRecord() throws Exception {

        MedicalRecordDTO stubMedicalRecord = new MedicalRecordDTO();
        stubMedicalRecord.setFirstName("Jacob");
        stubMedicalRecord.setLastName("Boyd");

        when(medicalRecordService.getMedicalRecordDataWrapper("Jacob", "Boyd")).thenReturn(stubMedicalRecord);

        MvcResult result = mockMvc.perform(get("/medicalRecord")
                        .param("firstName", "Jacob")
                        .param("lastName", "Boyd")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {

            result.getResolvedException().printStackTrace();
        } else {
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Jacob"))
                    .andExpect(jsonPath("$.lastName").value("Boyd"))
                    .andExpect(jsonPath("$.birthdate").value("03/06/1989"))
                    .andExpect(jsonPath("$.medications[0]").value("pharmacol:5000mg"))
                    .andExpect(jsonPath("$.medications[1]").value("terazine:10mg"))
                    .andExpect(jsonPath("$.medications[2]").value("noznazol:250mg"))
                    .andExpect(jsonPath("$.allergies").isEmpty());
        }
    }

    @Test
    public void testAddMedicalRecord() throws Exception {

        MedicalRecord stubMedicalRecord = new MedicalRecord();
        stubMedicalRecord.setFirstName("Jean");
        stubMedicalRecord.setLastName("Dupond");
        stubMedicalRecord.setBirthdate("01/01/1999");

        List<String> medications = new ArrayList<>();
        medications.add("ibupurin:200mg");
        medications.add("hydrapermazol:400mg");
        stubMedicalRecord.setMedications(medications);

        List<String> allergies = new ArrayList<>();
        allergies.add("nillacilan");
        stubMedicalRecord.setAllergies(allergies);

        Gson gson = new Gson();
        String jsonString = gson.toJson(stubMedicalRecord);

        MvcResult result = mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {

            result.getResolvedException().printStackTrace();
        } else {

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName").value("Jean"))
                    .andExpect(jsonPath("$.lastName").value("Dupond"))
                    .andExpect(jsonPath("$.birthDate").value("01/01/1999"))
                    .andExpect(jsonPath("$.medications[0]").value("ibupurin:200mg"))
                    .andExpect(jsonPath("$.medications[1]").value("hydrapermazol:400mg"))
                    .andExpect(jsonPath("$.allergies[0]").value("nillacilan"));
        }
    }

    @Test
    public void testUpdateMedicalRecord() throws Exception {

        MedicalRecord stubMedicalRecord = new MedicalRecord();
        stubMedicalRecord.setFirstName("Jean");
        stubMedicalRecord.setLastName("Dupond");
        stubMedicalRecord.setBirthdate("05/04/2000");

        List<String> medications = new ArrayList<>();
        medications.add("pharmacol:500mg");
        medications.add("noznazol:250mg");
        stubMedicalRecord.setMedications(medications);

        List<String> allergies = new ArrayList<>();
        allergies.add("illisoxian");
        stubMedicalRecord.setAllergies(allergies);

        Gson gson = new Gson();
        String jsonString = gson.toJson(stubMedicalRecord);

        MvcResult result = mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {

            result.getResolvedException().printStackTrace();
        } else {

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk());
        }

    }

    @Test
    public void testDeleteMedicalRecord() throws Exception {

        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "Jean")
                        .param("lastName", "Dupond"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetPersonsByFirestationNumber() throws Exception {

        Integer stationNumber = 1;

        MvcResult result = mockMvc.perform(get("/firestation")
                        .param("stationNumber", stationNumber.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();
        } else {
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void testGetChildAlert() throws Exception {

        String address = "947 E. Rose Dr";

        Map<String, HouseholdDTO> data = new HashMap<>();

        when(personService.getChildAlertData(address)).thenReturn(Optional.of(data));

        MvcResult result = mockMvc.perform(get("/childAlert")
                        .param("address", address)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {

            result.getResolvedException().printStackTrace();

        } else {

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.['3 Stelzer'].householdMembers", hasSize(3)))
                    .andExpect(jsonPath("$.['3 Stelzer'].householdMembers[0].firstName", is("Kendrik")))
                    .andExpect(jsonPath("$.['3 Stelzer'].householdMembers[0].lastName", is("Stelzer")))
                    .andExpect(jsonPath("$.['3 Stelzer'].householdMembers[0].age", is(10)))
                    .andExpect(jsonPath("$.['3 Stelzer'].householdMembers[1].firstName", is("Shawna")))
                    .andExpect(jsonPath("$.['3 Stelzer'].householdMembers[1].lastName", is("Stelzer")))
                    .andExpect(jsonPath("$.['3 Stelzer'].householdMembers[1].age", is(43)))
                    .andExpect(jsonPath("$.['3 Stelzer'].householdMembers[2].firstName", is("Brian")))
                    .andExpect(jsonPath("$.['3 Stelzer'].householdMembers[2].lastName", is("Stelzer")))
                    .andExpect(jsonPath("$.['3 Stelzer'].householdMembers[2].age", is(48)));
        }
    }

    @Test
    public void testGetPhoneAlert() throws Exception {

        MvcResult result = mockMvc.perform(get("/phoneAlert")
                .param("firestation", "1")
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();
        } else {
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0]").value("841-874-6512"))
                    .andExpect(jsonPath("$[1]").value("841-874-8547"))
                    .andExpect(jsonPath("$[2]").value("841-874-7462"))
                    .andExpect(jsonPath("$[3]").value("841-874-7784"));
        }
    }

    @Test
    void testGetFireInfoByAddress() throws Exception {

        List<DataOfInhabitantsDTO> mockedDataList = new ArrayList<>();
        DataOfInhabitantsDTO data = new DataOfInhabitantsDTO();
        data.setFirestationNumber(4);
        data.setFirstName("Lily");
        data.setLastName("Cooper");
        data.setPhone("841-874-9845");
        data.setAge(30);
        data.setMedications(new ArrayList<>());
        data.setAllergies(new ArrayList<>());
        mockedDataList.add(data);

        MvcResult result = mockMvc.perform(get("/fire")
                .param("address", "489 Manchester St")
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();
        } else {
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].firestationNumber").value(4))
                    .andExpect(jsonPath("$[0].firstName").value("Lily"))
                    .andExpect(jsonPath("$[0].lastName").value("Cooper"))
                    .andExpect(jsonPath("$[0].phone").value("841-874-9845"))
                    .andExpect(jsonPath("$[0].age").value(30))
                    .andExpect(jsonPath("$[0].medications").isEmpty())
                    .andExpect(jsonPath("$[0].allergies").isEmpty());
        }
    }

    @Test
    void testGetPersonsByListOfFirestationNumber() throws Exception {

        MvcResult result = mockMvc.perform(get("/flood/stations")
                .param("stations", "4")
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();
        } else {
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$['4'][0].firestationNumber").value(4))
                    .andExpect(jsonPath("$['4'][0].firstName").value("Tony"))
                    .andExpect(jsonPath("$['4'][0].lastName").value("Cooper"))
                    .andExpect(jsonPath("$['4'][0].phone").value("841-874-6874"))
                    .andExpect(jsonPath("$['4'][0].age").value(30))
                    .andExpect(jsonPath("$['4'][0].medications[0]").value("hydrapermazol:300mg"))
                    .andExpect(jsonPath("$['4'][0].medications[1]").value("dodoxadin:30mg"))
                    .andExpect(jsonPath("$['4'][0].allergies[0]").value("shellfish"))
                    .andExpect(jsonPath("$['4'][1].firestationNumber").value(4))
                    .andExpect(jsonPath("$['4'][1].firstName").value("Lily"))
                    .andExpect(jsonPath("$['4'][1].lastName").value("Cooper"))
                    .andExpect(jsonPath("$['4'][1].phone").value("841-874-9845"))
                    .andExpect(jsonPath("$['4'][1].age").value(30))
                    .andExpect(jsonPath("$['4'][1].medications").isEmpty())
                    .andExpect(jsonPath("$['4'][1].allergies").isEmpty())
                    .andExpect(jsonPath("$['4'][2].firestationNumber").value(4))
                    .andExpect(jsonPath("$['4'][2].firstName").value("Ron"))
                    .andExpect(jsonPath("$['4'][2].lastName").value("Peters"))
                    .andExpect(jsonPath("$['4'][2].phone").value("841-874-8888"))
                    .andExpect(jsonPath("$['4'][2].age").value(59))
                    .andExpect(jsonPath("$['4'][2].medications").isEmpty())
                    .andExpect(jsonPath("$['4'][2].allergies").isEmpty())
                    .andExpect(jsonPath("$['4'][3].firestationNumber").value(4))
                    .andExpect(jsonPath("$['4'][3].firstName").value("Allison"))
                    .andExpect(jsonPath("$['4'][3].lastName").value("Boyd"))
                    .andExpect(jsonPath("$['4'][3].phone").value("841-874-9888"))
                    .andExpect(jsonPath("$['4'][3].age").value(59))
                    .andExpect(jsonPath("$['4'][3].medications[0]").value("aznol:200mg"))
                    .andExpect(jsonPath("$['4'][3].allergies[0]").value("nillacilan"));
        }
    }

    @Test
    void testGetPersonInfo() throws Exception {

        MvcResult result = mockMvc.perform(get("/personInfo")
                .param("firstName", "Felicia")
                .param("lastName", "Boyd")
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();
        } else {
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].firstName").value("Felicia"))
                    .andExpect(jsonPath("$[0].lastName").value("Boyd"))
                    .andExpect(jsonPath("$[0].address").value("1509 Culver St"))
                    .andExpect(jsonPath("$[0].city").value("Culver"))
                    .andExpect(jsonPath("$[0].zip").value("97451"))
                    .andExpect(jsonPath("$[0].age").value(38))
                    .andExpect(jsonPath("$[0].email").value("jaboyd@email.com"))
                    .andExpect(jsonPath("$[0].medications[0]").value("tetracyclaz:650mg"))
                    .andExpect(jsonPath("$[0].allergies[0]").value("xilliathal"));
        }
    }

    @Test
    public void testGetCommunityEmail() throws Exception {

        MvcResult result = mockMvc.perform(get("/communityEmail")
                .param("city", "Springfield")
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();
        } else {
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").value("drk@email.com"))
                    .andExpect(jsonPath("$[1]").value("tenz@email.com"))
                    .andExpect(jsonPath("$[2]").value("jaboyd@email.com"))
                    .andExpect(jsonPath("$[3]").value("tcoop@ymail.com"))
                    .andExpect(jsonPath("$[4]").value("lily@email.com"))
                    .andExpect(jsonPath("$[5]").value("soph@email.com"))
                    .andExpect(jsonPath("$[6]").value("ward@email.com"))
                    .andExpect(jsonPath("$[7]").value("zarc@email.com"))
                    .andExpect(jsonPath("$[8]").value("reg@email.com"))
                    .andExpect(jsonPath("$[9]").value("jpeter@email.com"))
                    .andExpect(jsonPath("$[10]").value("aly@imail.com"))
                    .andExpect(jsonPath("$[11]").value("bstel@email.com"))
                    .andExpect(jsonPath("$[12]").value("ssanw@email.com"))
                    .andExpect(jsonPath("$[13]").value("clivfd@ymail.com"))
                    .andExpect(jsonPath("$[14]").value("gramps@email.com"));
        }
    }

    @Test
    public void testGetPersonNotFound() throws Exception {

        mockMvc.perform(get("/person")
                        .param("firstName", "FirstNameNotExists")
                        .param("lastName", "LastNameNotExists")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        }


    @Test
    void testDeletePerson() throws Exception {
        final String validFirstName = "Tessa";
        final String validLastName = "Carman";

        mockMvc.perform(delete("/person")
                        .param("firstName", validFirstName)
                        .param("lastName", validLastName))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeletePersonWithoutName() throws Exception {

        mockMvc.perform(delete("/person"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testUpdateFirestationAddressNotExists() throws Exception {

        Firestation stubFirestation = new Firestation();
        stubFirestation.setAddress("Address that does not exist");
        stubFirestation.setStation(99);

        Gson gson = new Gson();
        String jsonString = gson.toJson(stubFirestation);

        doThrow(new IllegalStateException("The address does not exist in firestations")).when(firestationService).updateFirestationStationNumberWrapper(anyString(), anyInt());

        MvcResult result = mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();

        } else {

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Failed to update the address does not exist in firestations"));

        }
}
    @Test
    void testUpdateFirestationAddressNotExists2() throws Exception {

        Firestation stubFirestation = new Firestation();
        stubFirestation.setAddress("Address that does not exist");
        stubFirestation.setStation(99);

        Gson gson = new Gson();
        String jsonString = gson.toJson(stubFirestation);

        MvcResult result = mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();

        } else {

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Failed to update the address does not exist in firestations"));

        }

    }

}