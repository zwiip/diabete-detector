package com.medilabo.assessment_service.controller;

import com.medilabo.assessment_service.dto.AssessmentDiabetesDTO;
import com.medilabo.assessment_service.service.AssessmentDiabetesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    private static final Logger log = LoggerFactory.getLogger(AssessmentController.class);
    private final AssessmentDiabetesService assessmentDiabetesService;

    public AssessmentController(AssessmentDiabetesService assessmentDiabetesService) {
        this.assessmentDiabetesService = assessmentDiabetesService;
    }

    /**
     * Retrieves the Diabetes assessment for a specific Patient id.
     * @param id Integer of the patient unique identifier.
     * @return the AssessmentDiabetesDTO for this patient.
     */
    @GetMapping("/{id}")
    public AssessmentDiabetesDTO getAssessmentDiabetesByPatientId(@PathVariable Integer id) {
        log.info("Fetching the assessment for the patient's id: {}", id);
        return assessmentDiabetesService.getPatientDiabetesRiskAssessment(id);
    }
}
