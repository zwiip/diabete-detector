package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.dto.AssessmentDiabetesDTO;
import com.medilabo.assessment_service.dto.NoteDTO;
import com.medilabo.assessment_service.dto.PatientDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AssessmentDiabetesService {

    @Value("${gateway.url}")
    private String gatewayUrl;
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(AssessmentDiabetesService.class);
    private static final Set<String> TRIGGER_WORDS_DIABETES = Set.of(
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
    );

    public AssessmentDiabetesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AssessmentDiabetesDTO getPatientDiabetesRiskAssessment(Integer id) {
        log.info("Creating assessment diabetes for patient n°{}", id);
        PatientDTO patient = getPatientInfo(id);
        NoteDTO[] notes = getPatientNotes(id);
        List<String> notesWords = getCleanedNotes(notes);
        int triggersWordsCount = getTriggersWordsCount(notesWords);
        int patientAge = getAge(patient.getBirthDate());
        PatientDTO.Gender patientGender = patient.getGender();
        log.debug("Patient information for this assessment : age={}, gender={}, number of notes={}, triggers'words count={} ", patientAge, patientGender, notes.length, triggersWordsCount);

        AssessmentDiabetesDTO assessment = new AssessmentDiabetesDTO();
        assessment.setPatientId(id);

        if (triggersWordsCount <= 1) {
            log.debug("Triggers words count is {}, that is less equal to one word, it's a none", triggersWordsCount);
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.NONE);
        } else if (patientAge >= 30) {
            log.debug("this patient is {}, this is older or equal to 30", patientAge);
            if (triggersWordsCount <= 5) {
                log.debug("Triggers words = {}, it's less or equal to 5. So it's a borderline", triggersWordsCount);
                assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.BORDERLINE);
            } else if (triggersWordsCount <= 7) {
                log.debug("Triggers words = {}, it's less or equal to 7. So it's a in danger", triggersWordsCount);
                assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.INDANGER);
            } else {
                log.debug("Triggers words = {}, it's more than 7. So it's a earlyonset", triggersWordsCount);
                assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.EARLYONSET);
            }
        } else if (patientGender == PatientDTO.Gender.MALE) {
            log.debug("this patient is {}, this is younger than 30 and it's a male = {}", patientAge, patientGender);
            if (triggersWordsCount < 5) {
                log.debug("Triggers words = {}, it's more or equal to 5. So it's a earlyonset", triggersWordsCount);
                assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.EARLYONSET);
            } else {
                log.debug("Triggers words = {}, it equals to 3. So it's a in danger", triggersWordsCount);
                assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.INDANGER);
            }
        } else if (patientGender == PatientDTO.Gender.FEMALE) {
            log.debug("this patient is {}, this is younger than 30 and it's a female = {}", patientAge, patientGender);
            if (triggersWordsCount >= 7) {
                log.debug("Triggers words = {}, it's more or equal to 7. So it's a earlyonset", triggersWordsCount);
                assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.EARLYONSET);
            } else if (triggersWordsCount >= 4) {
                log.debug("Triggers words = {}, it's more or equal to 4'. So it's a in danger", triggersWordsCount);
                assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.INDANGER);
            }
        } else {
            log.debug("nothing special to record here");
            assessment.setRiskLevel(AssessmentDiabetesDTO.RiskLevel.NONE);
        }
        log.info("Risk assessment = {}", assessment.getRiskLevel());
        return assessment;
    }

    private PatientDTO getPatientInfo(Integer id) {
        log.debug("Fetching the patient information for the id: {}", id);
        return restTemplate.getForObject(
                gatewayUrl + "/patients/" + id,
                PatientDTO.class
        );
    }

    private NoteDTO[] getPatientNotes(Integer id) {
        log.debug("Fetching notes for the id: {}", id);
        return restTemplate.getForObject(
                gatewayUrl + "/notes/" + id,
                NoteDTO[].class
        );
    }

    private List<String> getCleanedNotes(NoteDTO[] notes) {
        if (notes == null || notes.length == 0) {
            log.warn("No notes to clean.");
            return List.of();
        }

        log.debug("Cleaning {} notes", notes.length);
        List<String> wordsFromNotes = new ArrayList<>();

        for (NoteDTO note : notes) {
            String noteText = note.getNoteText();
            if (noteText == null || noteText.isBlank()) {
                continue;
            }

            log.debug("getting note text : {}", noteText);
            String cleanedText = normalizeText(noteText);
            String[] words = cleanedText.split("\\W+");
            wordsFromNotes.addAll(List.of(words));
            log.debug("Here are the words : {}", wordsFromNotes);
        }
        log.debug("Notes cleaned into {} words", wordsFromNotes.size());
        return wordsFromNotes;
    }

    private String normalizeText(String textToNormalize) {
        return  Normalizer.normalize(textToNormalize, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    private int getAge(LocalDate birthdate) {
        log.debug("Calculating age for the date: {}", birthdate);
        return Period.between(
                birthdate,
                LocalDate.now()
        ).getYears();
    }

    private int getTriggersWordsCount(List<String> notesWords) {
        log.debug("starting count for this text ={}", notesWords);

        int triggersWordsCount = 0;

        for (String triggerWord : TRIGGER_WORDS_DIABETES.toArray(new String[0])) {
            String normalizedTrigger = normalizeText(triggerWord);

            boolean found = notesWords.stream().anyMatch(word ->
                    word.startsWith(normalizedTrigger)
            || normalizedTrigger.startsWith(word)
            );

            if (found) {
                triggersWordsCount ++;
                log.debug("number ={}, for the triggerWord ={}", triggersWordsCount, triggerWord);
            }
        }

        log.debug("final number ={}", triggersWordsCount);
        return triggersWordsCount;
    }
}