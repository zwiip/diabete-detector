package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.dto.AssessmentDiabetesDTO;
import com.medilabo.assessment_service.dto.NoteDTO;
import com.medilabo.assessment_service.dto.PatientDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentDiabetesServiceTest {

    @Mock
    private GatewayClient gatewayClient;

    @InjectMocks
    private AssessmentDiabetesService service;

    // =========================
    // Helpers
    // =========================

    private PatientDTO buildPatient(int age, PatientDTO.Gender gender) {
        PatientDTO patient = new PatientDTO();
        patient.setBirthDate(LocalDate.now().minusYears(age));
        patient.setGender(gender);
        return patient;
    }

    private NoteDTO[] buildNotes(String text) {
        return new NoteDTO[]{ new NoteDTO(text) };
    }

    // =========================
    // NONE (<=1 trigger)
    // =========================

    @Test
    void shouldReturnNone_whenNoTriggerWords() {
        Integer id = 1;

        when(gatewayClient.get("/patients/" + id, PatientDTO.class, id))
                .thenReturn(buildPatient(50, PatientDTO.Gender.MALE));

        when(gatewayClient.get("/notes/" + id, NoteDTO[].class, id))
                .thenReturn(buildNotes("Rien de particulier"));

        AssessmentDiabetesDTO result = service.getPatientDiabetesRiskAssessment(id);

        assertEquals(AssessmentDiabetesDTO.RiskLevel.NONE, result.getRiskLevel());
    }

    @Test
    void shouldReturnNone_whenOneTriggerWord() {
        Integer id = 2;

        when(gatewayClient.get("/patients/" + id, PatientDTO.class, id))
                .thenReturn(buildPatient(50, PatientDTO.Gender.MALE));

        when(gatewayClient.get("/notes/" + id, NoteDTO[].class, id))
                .thenReturn(buildNotes("Poids"));

        AssessmentDiabetesDTO result = service.getPatientDiabetesRiskAssessment(id);

        assertEquals(AssessmentDiabetesDTO.RiskLevel.NONE, result.getRiskLevel());
    }

    // =========================
    // >= 30 YEARS OLD
    // =========================

    @Test
    void shouldReturnBorderline_whenOver30_and2To5Triggers() {
        Integer id = 3;

        when(gatewayClient.get("/patients/" + id, PatientDTO.class, id))
                .thenReturn(buildPatient(45, PatientDTO.Gender.MALE));

        when(gatewayClient.get("/notes/" + id, NoteDTO[].class, id))
                .thenReturn(buildNotes("Poids Cholestérol Vertiges"));

        AssessmentDiabetesDTO result = service.getPatientDiabetesRiskAssessment(id);

        assertEquals(AssessmentDiabetesDTO.RiskLevel.BORDERLINE, result.getRiskLevel());
    }

    @Test
    void shouldReturnInDanger_whenOver30_and6To7Triggers() {
        Integer id = 4;

        when(gatewayClient.get("/patients/" + id, PatientDTO.class, id))
                .thenReturn(buildPatient(45, PatientDTO.Gender.MALE));

        when(gatewayClient.get("/notes/" + id, NoteDTO[].class, id))
                .thenReturn(buildNotes(
                        "Poids Cholestérol Vertiges Rechute Réaction Anticorps"
                ));

        AssessmentDiabetesDTO result = service.getPatientDiabetesRiskAssessment(id);

        assertEquals(AssessmentDiabetesDTO.RiskLevel.INDANGER, result.getRiskLevel());
    }

    @Test
    void shouldReturnEarlyOnset_whenOver30_and8OrMoreTriggers() {
        Integer id = 5;

        when(gatewayClient.get("/patients/" + id, PatientDTO.class, id))
                .thenReturn(buildPatient(45, PatientDTO.Gender.MALE));

        when(gatewayClient.get("/notes/" + id, NoteDTO[].class, id))
                .thenReturn(buildNotes(
                        "Poids Cholestérol Vertiges Rechute Réaction Anticorps Taille Fumeur"
                ));

        AssessmentDiabetesDTO result = service.getPatientDiabetesRiskAssessment(id);

        assertEquals(AssessmentDiabetesDTO.RiskLevel.EARLYONSET, result.getRiskLevel());
    }

    // =========================
    // < 30 YEARS OLD - MALE
    // =========================

    @Test
    void shouldReturnInDanger_whenMaleUnder30_and3To4Triggers() {
        Integer id = 6;

        when(gatewayClient.get("/patients/" + id, PatientDTO.class, id))
                .thenReturn(buildPatient(25, PatientDTO.Gender.MALE));

        when(gatewayClient.get("/notes/" + id, NoteDTO[].class, id))
                .thenReturn(buildNotes("Poids Cholestérol Vertiges"));

        AssessmentDiabetesDTO result = service.getPatientDiabetesRiskAssessment(id);

        assertEquals(AssessmentDiabetesDTO.RiskLevel.INDANGER, result.getRiskLevel());
    }

    @Test
    void shouldReturnEarlyOnset_whenMaleUnder30_and5OrMoreTriggers() {
        Integer id = 7;

        when(gatewayClient.get("/patients/" + id, PatientDTO.class, id))
                .thenReturn(buildPatient(25, PatientDTO.Gender.MALE));

        when(gatewayClient.get("/notes/" + id, NoteDTO[].class, id))
                .thenReturn(buildNotes("Poids Cholestérol Vertiges Rechute Réaction"));

        AssessmentDiabetesDTO result = service.getPatientDiabetesRiskAssessment(id);

        assertEquals(AssessmentDiabetesDTO.RiskLevel.EARLYONSET, result.getRiskLevel());
    }

    // =========================
    // < 30 YEARS OLD - FEMALE
    // =========================

    @Test
    void shouldReturnInDanger_whenFemaleUnder30_and4To6Triggers() {
        Integer id = 8;

        when(gatewayClient.get("/patients/" + id, PatientDTO.class, id))
                .thenReturn(buildPatient(25, PatientDTO.Gender.FEMALE));

        when(gatewayClient.get("/notes/" + id, NoteDTO[].class, id))
                .thenReturn(buildNotes("Poids Cholestérol Vertiges Rechute"));

        AssessmentDiabetesDTO result = service.getPatientDiabetesRiskAssessment(id);

        assertEquals(AssessmentDiabetesDTO.RiskLevel.INDANGER, result.getRiskLevel());
    }

    @Test
    void shouldReturnEarlyOnset_whenFemaleUnder30_and7OrMoreTriggers() {
        Integer id = 9;

        when(gatewayClient.get("/patients/" + id, PatientDTO.class, id))
                .thenReturn(buildPatient(25, PatientDTO.Gender.FEMALE));

        when(gatewayClient.get("/notes/" + id, NoteDTO[].class, id))
                .thenReturn(buildNotes(
                        "Poids Cholestérol Vertiges Rechute Réaction Anticorps Taille"
                ));

        AssessmentDiabetesDTO result = service.getPatientDiabetesRiskAssessment(id);

        assertEquals(AssessmentDiabetesDTO.RiskLevel.EARLYONSET, result.getRiskLevel());
    }

    // =========================
    // Exception
    // =========================

    @Test
    void shouldThrowException_whenPatientDataIncomplete() {
        Integer id = 10;

        PatientDTO patient = new PatientDTO(); // missing birthDate & gender

        when(gatewayClient.get("/patients/" + id, PatientDTO.class, id))
                .thenReturn(patient);

        assertThrows(IllegalStateException.class,
                () -> service.getPatientDiabetesRiskAssessment(id));
    }
}
