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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    private PatientService patientService;

    private Patient patientNone;
    private Patient patientBorderline;
    private Patient patientInDanger;
    private Patient patientEarlyOnset;
    List<Patient> patients = new ArrayList<>();

    @Mock
    private PatientRepository patientRepository;

    @BeforeEach
    public void setup() {
        patientService = new PatientService(patientRepository);

        patientNone = new Patient("TestNone", "Test", LocalDate.of(1966, 12, 31), Patient.Gender.FEMALE, "1 Brookside St", "100-222-3333");
        patientBorderline = new Patient("TestBorderline", "Test", LocalDate.of(1945, 06, 24), Patient.Gender.MALE, "2 High St", "200-333-4444");
        patientInDanger = new Patient("TestInDanger", "Test", LocalDate.of(2004, 06, 18), Patient.Gender.MALE, "3 Club Road", "300-444-5555");
        patientEarlyOnset = new Patient("TestEarlyOnset", "Test", LocalDate.of(2002, 06, 28), Patient.Gender.FEMALE, "4 Valley Dr", "400-555-6666");

        patients.add(patientNone);
        patients.add(patientBorderline);
        patients.add(patientInDanger);
        patients.add(patientEarlyOnset);
    }

    @Test
    void givenFourPatients_whenGetAllPatients_shouldReturnFourPatients() {
        when(patientRepository.findAll()).thenReturn(patients);
        assertEquals(4, patientService.getAllPatients().size());
    }

    @Test
    void givenAnExistingID_whenGetPatientById_shouldReturnTheCorrectPatient() {
        when(patientRepository.findById(1)).thenReturn(Optional.ofNullable(patientNone));

        Patient patient = patientService.getPatientById(1);

        assertNotNull(patient);
        assertEquals("TestNone", patient.getName());
    }

    @Test
    void givenPartialName_whenSearchPatients_shouldReturnTwoPatients() {
        when(patientRepository.findAll()).thenReturn(patients);
        List<Patient> patientsWithIN = patientService.searchPatients("in", null, null, null);
        assertEquals(2, patientsWithIN.size());
    }

    @Test
    void givenAFullName_whenSearchPatients_shouldReturnTheMatchingPatient() {
        when(patientRepository.findAll()).thenReturn(patients);
        List<Patient> patientNamedTestBorderline = patientService.searchPatients("TestBorderline", null, null, null);
        assertEquals(1, patientNamedTestBorderline.size());
    }

    @Test
    void givenPartialFirstName_whenSearchPatients_shouldReturnTheMatchingPatients() {
        when(patientRepository.findAll()).thenReturn(patients);
        List<Patient> patientWithTE = patientService.searchPatients(null, "Te", null, null);
        assertEquals(4, patientWithTE.size());
    }

    @Test
    void givenFirstName_whenSearchPatients_shouldReturnTheMatchingPatients() {
        when(patientRepository.findAll()).thenReturn(patients);
        List<Patient> patientNamedTest = patientService.searchPatients(null, "Test", null, null);
        assertEquals(4, patientNamedTest.size());
    }

    @Test
    void givenFemaleGender_whenSearchPatients_thenReturnMatchingPatients() {
        when(patientRepository.findAll()).thenReturn(patients);
        List<Patient> femalePatients = patientService.searchPatients(null, null, null, Patient.Gender.FEMALE);
        assertEquals(2, femalePatients.size());
    }

    @Test
    void givenAMatchingDate_whenSearchPatients_thenReturnMatchingPatient() {
        when(patientRepository.findAll()).thenReturn(patients);
        List<Patient> patient = patientService.searchPatients(null, null, LocalDate.of(2004, 06, 18), null);
        assertEquals(1, patient.size());
    }

    @Test
    void givenNoCriteria_whenSearchPatients_thenReturnAllPatients() {
        when(patientRepository.findAll()).thenReturn(patients);
        List<Patient> resultWithoutFilter = patientService.searchPatients(null, null, null, null);
        assertEquals(4, resultWithoutFilter.size());
    }
}
