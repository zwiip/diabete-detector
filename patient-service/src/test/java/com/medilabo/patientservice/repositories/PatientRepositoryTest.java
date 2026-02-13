package com.medilabo.patientservice.repositories;

import com.medilabo.patientservice.entities.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    private Patient patientNone;
    private Patient patientBorderline;
    private Patient patientInDanger;
    private Patient patientEarlyOnset;

    @BeforeEach
    public void setup() {
        patientNone = new Patient("TestNone", "Test", LocalDate.of(1966,12,31), Patient.Gender.FEMALE, "1 Brookside St", "100-222-3333");
        patientBorderline = new Patient("TestBorderline", "Test", LocalDate.of(1945,06,24), Patient.Gender.MALE, "2 High St", "200-333-4444");
        patientInDanger = new Patient("TestInDanger", "Test", LocalDate.of(2004,06,18), Patient.Gender.MALE, "3 Club Road", "300-444-5555");
        patientEarlyOnset = new Patient("TestEarlyOnset", "Test", LocalDate.of(2002,06,28), Patient.Gender.FEMALE, "4 Valley Dr", "400-555-6666");

        patientRepository.deleteAll();
        patientRepository.save(patientNone);
        patientRepository.save(patientBorderline);
        patientRepository.save(patientInDanger);
        patientRepository.save(patientEarlyOnset);
    }

    @Test
    void savedPatientIDShouldNotBeNull() {
        assertNotNull(patientNone.getId());
    }

    @Test
    void findAllPatientsShouldReturnFour() {
        List<Patient> patients = patientRepository.findAll();
        assertEquals(4, patients.size());
    }

}