package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.dto.AssessmentDiabetesDTO;
import com.medilabo.assessment_service.dto.NoteDTO;
import com.medilabo.assessment_service.dto.PatientDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * Service responsible for assessing the diabetes risk level of a patient.
 * This service aggregates patient information and medical notes
 * retrieved through the gateway, analyzes the presence of predefined
 * trigger words in medical notes, and determines the patient's diabetes
 * risk level according to given logical rules.
 */

@Service
public class AssessmentDiabetesService {

    private final GatewayClient gatewayClient;
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

    public AssessmentDiabetesService(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    /**
     * Assess the diabetes risk for a given patient.
     * The method uses several private methods in order to gather the patient information and his medical notes that are compared to a list of trigger words .
     * Then it applies a logical algorithm to determine the risk.
     * @param id, the unique identifier of the patient.
     * @return a AssessmentDiabetesDTO object containing the id of the patient and his risk level.
     */
    public AssessmentDiabetesDTO getPatientDiabetesRiskAssessment(Integer id) {
        log.info("Creating assessment diabetes for patient n°{}", id);
        PatientDTO patient = getPatientInfo(id);
        NoteDTO[] notes = getPatientNotes(id);
        List<String> notesWords = getCleanedNotes(notes);
        int triggersWordsCount = getTriggersWordsCount(notesWords);
        int patientAge = getAge(patient.getBirthDate());
        PatientDTO.Gender patientGender = patient.getGender();

        AssessmentDiabetesDTO assessment = new AssessmentDiabetesDTO();
        assessment.setPatientId(id);
        assessment.setRiskLevel(determineRisk(patientAge, patientGender, triggersWordsCount));
        log.info("Risk assessment = {}", assessment.getRiskLevel());

        return assessment;
    }

    /**
     * Determines the risk level according to given rules for a specific patient.
     * These rules have been slightly modified in order to be more logic.
     * Comments are spread through the algorithm to explain these changes.
     * @param patientAge int representing the age of the patient
     * @param patientGender enum to define the gender of the patient
     * @param triggersWordsCount the number of triggers for this patient
     * @return the riskLevel for the patient.
     */
    private AssessmentDiabetesDTO.RiskLevel determineRisk(int patientAge, PatientDTO.Gender patientGender, int triggersWordsCount) {
        log.debug("Patient information for this assessment : age={}, gender={}, triggers'words count={} ", patientAge, patientGender, triggersWordsCount);

        AssessmentDiabetesDTO.RiskLevel riskLevel = AssessmentDiabetesDTO.RiskLevel.NONE;

        // asked triggers == 0, but, nothing in particular is given for 1 (which is also NONE then).
        if (triggersWordsCount <= 1) {
            log.debug("Triggers words count is {}, that is less equal to one word, it's a NONE", triggersWordsCount);

        } else if (patientAge >= 30) {
            log.debug("this patient is {}, this is older or equal to 30", patientAge);
            if (triggersWordsCount <= 5) {
                log.debug("Triggers words = {}, it's less or equal to 5. But more than 1. So it's a BORDERLINE", triggersWordsCount);
                riskLevel = AssessmentDiabetesDTO.RiskLevel.BORDERLINE;
            } else if (triggersWordsCount <= 7) {
                log.debug("Triggers words = {}, it's less or equal to 7 but more than 5. So it's an INDANGER", triggersWordsCount);
                riskLevel = AssessmentDiabetesDTO.RiskLevel.INDANGER;
            } else {
                log.debug("Triggers words = {}, it's more than 7. So it's a EARLYONSET", triggersWordsCount);
                riskLevel = AssessmentDiabetesDTO.RiskLevel.EARLYONSET;
            }

        } else if (patientGender == PatientDTO.Gender.MALE) {
            log.debug("this patient is {}, this is younger than 30 and it's a male = {}", patientAge, patientGender);
            // "EARLYONSET" is for triggers >= 5, so the else represents "INDANGER". Normally it is for triggers == 3, but no rule is defined for 4. Logically it's in the same group than 3.
            if (triggersWordsCount >= 5) {
                log.debug("Triggers words = {}, it's more or equals to 5. So it's a EARLYONSET", triggersWordsCount);
                riskLevel = AssessmentDiabetesDTO.RiskLevel.EARLYONSET;
            } else if (triggersWordsCount >= 3){
                log.debug("Triggers words = {}, it is more or equals to 3 but less than 5. So it's an INDANGER", triggersWordsCount);
                riskLevel = AssessmentDiabetesDTO.RiskLevel.INDANGER;
            }

        } else if (patientGender == PatientDTO.Gender.FEMALE) {
            log.debug("this patient is {}, this is younger than 30 and it's a female = {}", patientAge, patientGender);
            if (triggersWordsCount >= 7) {
                log.debug("Triggers words = {}, it's more or equal to 7. So it's a EARLYONSET", triggersWordsCount);
                riskLevel = AssessmentDiabetesDTO.RiskLevel.EARLYONSET;

            // INDANGER is for triggers == 4, but no rules are defined for 5 and 6, so it's logically in the same group.
            } else if (triggersWordsCount >= 4) {
                log.debug("Triggers words = {}, it's more or equal to 4, but less than 7'. So it's an INDANGER", triggersWordsCount);
                riskLevel = AssessmentDiabetesDTO.RiskLevel.INDANGER;
            }
        }

        return riskLevel;
    }

    /**
     * Retrieves patient information from the gateway
     * @param id, the patient unique identifier,
     * @return a PatientDTO object with the needed information (birthdate and gender).
     * @throws IllegalStateException if the patient information are null.
     */
    private PatientDTO getPatientInfo(Integer id) {
        log.debug("Fetching information for patient {}", id);
        PatientDTO patient = gatewayClient.get("/patients/" + id, PatientDTO.class, id);

        if (patient == null || patient.getBirthDate() == null || patient.getGender() == null) {
            throw new IllegalStateException("Incomplete patient data for id " + id);
        }

        return patient;
    }

    /**
     * Retrieves all medical notes associated to the patient.
     * @param id, the patient unique identifier,
     * @return an array with the NoteDTO objects containing the texts of the note. Can be empty.
     */
    private NoteDTO[] getPatientNotes(Integer id) {
        log.debug("Fetching notes for patient {}", id);
        NoteDTO[] notes = gatewayClient.get("/notes/" + id, NoteDTO[].class, id);
        return notes != null ? notes : new NoteDTO[0];
    }

    /**
     * Extracts and normalizes all words from the patient's medical notes.
     * @param notes, an array with the patient's medical notes
     * @return a List of words cleaned and extracted from the notes
     */
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

    /**
     * Normalizes a String by removing the accents and converting it to lowercase.
     * This method is used bith for medical notes and trigger words to ensure consistent comparisons.
     * @param textToNormalize, the raw String
     * @return a normalized version of the String.
     */
    private String normalizeText(String textToNormalize) {
        return  Normalizer.normalize(textToNormalize, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    /**
     * Calculates the patient's age in years, based on the birthDate.
     * @param birthDate, LocalDate of the patient's date of birth
     * @return an int representing the age in years.
     */
    private int getAge(LocalDate birthDate) {
        log.debug("Calculating age for the date: {}", birthDate);

        return Period.between(
                birthDate,
                LocalDate.now()
        ).getYears();
    }

    /**
     * Counts how many distinct trigger words are present in the patient's notes.
     * A trigger word is considered found if:
     * a word from the notes partially matches the normalized trigger word and vice versa,
     * This prefix-based matching allows singular/plural and minor variations.
     * However, it increases the number of matches if several similar word are used in the triggers list.
     * We recommend to add to the trigger list only word's stems.
     * @param notesWords, a List of String of word we want to check.
     * @return an int representing the number of matches with the triggers list.
     */
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