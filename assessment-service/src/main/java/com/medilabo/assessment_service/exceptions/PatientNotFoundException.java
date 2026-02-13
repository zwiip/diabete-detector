package com.medilabo.assessment_service.exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Integer id) {
        super("Patient with id " + id + " not found");
    }
}
