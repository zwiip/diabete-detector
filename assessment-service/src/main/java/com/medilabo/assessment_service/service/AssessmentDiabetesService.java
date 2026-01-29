package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.dto.AssessmentDiabetesDTO;
import com.medilabo.assessment_service.dto.PatientDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssessmentDiabetesService {


    public AssessmentDiabetesDTO getPatientDiabetesRiskAssessment(PatientDTO patient) {
        String notesText = getCleanedNotes(patient.getNotes());
        Integer triggersWordsCount = getTriggersWordsCount(notesText);
        Integer patientAge = patient.getAge();
        PatientDTO.Gender patientGender = patient.getGender();

        AssessmentDiabetesDTO assessment = new AssessmentDiabetesDTO();
        assessment.setPatientId(patient.getId());

        if (triggersWordsCount == 0) {
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.NONE);
            
        } else if (triggersWordsCount >= 2 && triggersWordsCount <= 5 && patientAge > 30) {
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.BORDERLINE);
            
        } else if ((triggersWordsCount == 6 || triggersWordsCount == 7 && patientAge > 30)
                    || (patientAge < 30 && patientGender == PatientDTO.Gender.MALE && triggersWordsCount == 3)
                    || (patientAge < 30 && patientGender == PatientDTO.Gender.FEMALE && triggersWordsCount == 4)) {
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.INDANGER);
        } else if ((patientAge > 30 && triggersWordsCount >= 8)
                    || (patientAge < 30 && patientGender == PatientDTO.Gender.MALE && triggersWordsCount >= 5)
                    || (patientAge < 30 && patientGender == PatientDTO.Gender.FEMALE && triggersWordsCount >= 7)) {
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.EARLYONSET);
        }

        return assessment;
    }

    private String getCleanedNotes(List<String> notes) {
        String notesText = "";
        for (String note : notes) {
            notesText += note;
        }
        return notesText.toLowerCase();
    }

    private Integer getTriggersWordsCount(String notesText) {
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

        Integer triggersWordsCount = 0;

        for (String word : triggerWordsDiabetes) {
            if (notesText.contains(word.toLowerCase())) {
                triggersWordsCount ++;
            }
        }

        return triggersWordsCount;
    }
}
