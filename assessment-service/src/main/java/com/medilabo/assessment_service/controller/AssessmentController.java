package com.medilabo.assessment_service.controller;

import com.medilabo.assessment_service.dto.AssessmentDiabetesDTO;
import com.medilabo.assessment_service.service.AssessmentDiabetesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    private final AssessmentDiabetesService assessmentDiabetesService;

    public AssessmentController(AssessmentDiabetesService assessmentDiabetesService) {
        this.assessmentDiabetesService = assessmentDiabetesService;
    }

    @GetMapping("/{id}")
    public AssessmentDiabetesDTO getAssessmentDiabetesByPatientId(@PathVariable Integer id) {
        return assessmentDiabetesService.getPatientDiabetesRiskAssessment(id);
    }
}
