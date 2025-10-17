package com.medilabo.patientservice.services;

import com.medilabo.patientservice.entities.Patient;
import com.medilabo.patientservice.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    private PatientService patientService;

    private Patient patientNone;
    private Patient patientBorderline;
    private Patient patientInDanger;
    private Patient patientEarlyOnset;

    @Mock
    private PatientRepository patientRepository;

    @BeforeEach
    public void setup() {
        patientService = new PatientService(patientRepository);

        patientNone = new Patient("TestNone", "Test", LocalDate.of(1966, 12, 31), Patient.Gender.FEMALE, "1 Brookside St", "100-222-3333");
        patientBorderline = new Patient("TestBorderline", "Test", LocalDate.of(1945, 06, 24), Patient.Gender.MALE, "2 High St", "200-333-4444");
        patientInDanger = new Patient("TestInDanger", "Test", LocalDate.of(2004, 06, 18), Patient.Gender.MALE, "3 Club Road", "300-444-5555");
        patientEarlyOnset = new Patient("TestEarlyOnset", "Test", LocalDate.of(2002, 06, 28), Patient.Gender.FEMALE, "4 Valley Dr", "400-555-6666");

    }

    @Test
    void givenFourPatients_whenGetAllPatients_shouldReturnFourPatients() {
        List<Patient> patients = new ArrayList<>();
        patients.add(patientNone);
        patients.add(patientBorderline);
        patients.add(patientInDanger);
        patients.add(patientEarlyOnset);
        when(patientRepository.findAll()).thenReturn(patients);

        assertEquals(4, patientService.getAllPatients().size());
    }

    @Test
    void givenAnExistingID_whenGetPatientById_shouldReturnTheCorrectPatient() {
        when(patientRepository.findById(anyInt())).thenReturn(Optional.ofNullable(patientNone));

        Optional<Patient> patient = patientService.getPatientById(1);

        assertNotNull(patient);
        assertEquals("TestNone", patient.get().getName());
    }

}
