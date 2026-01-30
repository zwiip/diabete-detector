package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.dto.AssessmentDiabetesDTO;
import com.medilabo.assessment_service.dto.NoteDTO;
import com.medilabo.assessment_service.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;

@Service
public class AssessmentDiabetesService {

    @Value("${gateway.url}")
    private String gatewayUrl;
    private final RestTemplate restTemplate;

    public AssessmentDiabetesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AssessmentDiabetesDTO getPatientDiabetesRiskAssessment(Integer id) {
        PatientDTO patient = getPatientInfo(id);
        NoteDTO[] notes = getPatientNotes(id);
        String notesText = getCleanedNotes(notes);
        int triggersWordsCount = getTriggersWordsCount(notesText);
        int patientAge = getAge(patient.getBirthDate());
        PatientDTO.Gender patientGender = patient.getGender();

        AssessmentDiabetesDTO assessment = new AssessmentDiabetesDTO();
        assessment.setPatientId(id);

        if (triggersWordsCount == 0) {
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.NONE);
            
        } else if (triggersWordsCount >= 2 && triggersWordsCount <= 5 && patientAge > 30) {
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.BORDERLINE);
            
        } else if (((triggersWordsCount == 6 || triggersWordsCount == 7) && patientAge > 30)
                    || (patientAge < 30 && patientGender == PatientDTO.Gender.MALE && triggersWordsCount == 3)
                    || (patientAge < 30 && patientGender == PatientDTO.Gender.FEMALE && triggersWordsCount == 4)) {
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.INDANGER);
        } else if ((patientAge > 30 && triggersWordsCount >= 8)
                    || (patientAge < 30 && patientGender == PatientDTO.Gender.MALE && triggersWordsCount >= 5)
                    || (patientAge < 30 && patientGender == PatientDTO.Gender.FEMALE && triggersWordsCount >= 7)) {
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.EARLYONSET);
        } else {
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.NONE);
        }

        return assessment;
    }

    private PatientDTO getPatientInfo(Integer id) {
        return restTemplate.getForObject(
                gatewayUrl + "/patients/" + id,
                PatientDTO.class);
    }

    private NoteDTO[] getPatientNotes(Integer id) {
        return restTemplate.getForObject(
                gatewayUrl + "/notes/patient/" + id,
                NoteDTO[].class
        );
    }

    private String getCleanedNotes(NoteDTO[] notes) {
        if (notes == null || notes.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (NoteDTO note : notes) {
            if (note.getNote() != null) {
                sb.append(note.getNote()).append(" ");
            }
        }

        return sb.toString().toLowerCase();
    }

    private int getAge(LocalDate birthdate) {
        return Period.between(
                birthdate,
                LocalDate.now()
        ).getYears();
    }

    private int getTriggersWordsCount(String notesText) {
        String[] triggerWordsDiabetes = {
            "Hémoglobine A1C",
            "Microalbumine",
            "Taille",
            "Poids",
            "Fumeur",
            "Fumeuse",
            "Anormal",
            "Cholestérol",
            "Vertiges",
            "Rechute",
            "Réaction",
            "Anticorps"
        };

        int triggersWordsCount = 0;

        for (String word : triggerWordsDiabetes) {
            if (notesText.contains(word.toLowerCase())) {
                triggersWordsCount ++;
            }
        }

        return triggersWordsCount;
    }
}
