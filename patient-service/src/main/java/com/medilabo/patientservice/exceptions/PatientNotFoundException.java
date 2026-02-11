package com.medilabo.patientservice.exceptions;

/**
 * Exception thrown when a patient cannot be found for a given identifier.
 */
public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Integer id) {
        super("Patient not found with id: " + id);
    }
}