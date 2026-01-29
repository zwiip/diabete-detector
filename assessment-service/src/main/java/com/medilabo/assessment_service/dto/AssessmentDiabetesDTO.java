package com.medilabo.assessment_service.dto;

public class AssessmentDiabetesDTO {

    Integer patientId;
    RiskLevel riskLevel;

    public enum RiskLevel {
        NONE, BORDERLINE, INDANGER, EARLYONSET
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
}
