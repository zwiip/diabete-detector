package com.medilabo.patientservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.patientservice.entities.Patient;
import com.medilabo.patientservice.repositories.PatientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${gateway.username}")
    private String gatewayUsername;

    @Value("${gateway.password}")
    private String gatewayPassword;

    private Patient patientNone;
    private Patient patientBorderline;
    private Patient patientInDanger;
    private Patient patientEarlyOnset;

    @BeforeEach
    void setup() {
        patientRepository.deleteAll();

        patientNone = new Patient("TestNone", "Test", LocalDate.of(1966,12,31), Patient.Gender.FEMALE, "1 Brookside St", "100-222-3333");
        patientBorderline = new Patient("TestBorderline", "Test", LocalDate.of(1945,06,24), Patient.Gender.MALE, "2 High St", "200-333-4444");
        patientInDanger = new Patient("TestInDanger", "Test", LocalDate.of(2004,06,18), Patient.Gender.MALE, "3 Club Road", "300-444-5555");
        patientEarlyOnset = new Patient("TestEarlyOnset", "Test", LocalDate.of(2002,06,28), Patient.Gender.FEMALE, "4 Valley Dr", "400-555-6666");

        patientRepository.save(patientNone);
        patientRepository.save(patientBorderline);
        patientRepository.save(patientInDanger);
        patientRepository.save(patientEarlyOnset);
    }

    @AfterEach
    void tearDown() {
        patientRepository.deleteAll();
    }

    @Test
    void whenNoAuthentication_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenFourPatients_whenGetAllPatients_returnFourPatients() throws Exception {
        mockMvc.perform(get("/patients")
                        .with(httpBasic(gatewayUsername,gatewayPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("TestNone"));
    }

    @Test
    void givenTwoPatientsFirstNameWithIN_whenSearchPatientsWithIn_shouldReturnTwoPatients() throws Exception {
        mockMvc.perform(get("/patients/search").param("name", "in")
                        .with(httpBasic(gatewayUsername,gatewayPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("TestBorderline"));
    }

    @Test
    void givenFourPatients_addPatient_shouldResultInFourPatients() throws Exception {

        mockMvc.perform(post("/patients")
                        .with(httpBasic(gatewayUsername,gatewayPassword))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                            "name": "New",
                            "firstName": "Patient",
                            "birthDate": "1989-03-03",
                            "gender": "FEMALE",
                            "address": "Main Street",
                            "phone": "1111111111"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.firstName").value("Patient"));

        assertEquals(5, patientRepository.count());
    }

    @Test
    void givenUnknownId_whenGetPatient_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/patients/9999")
                        .with(httpBasic(gatewayUsername,gatewayPassword)))
                .andExpect(status().isNotFound());
    }
}