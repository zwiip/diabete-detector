package com.medilabo.patientservice.controllers;

import com.medilabo.patientservice.entities.Patient;
import com.medilabo.patientservice.services.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Retrieves all patients.
     * @return a list of Patient object
     */
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    /**
     * Retrieves a list of patient that matches the optional criteria.
     * @param name patient last name
     * @param firstName patient first name
     * @param birthDate patient birthdate
     * @param gender patient gender
     * @return list of matching patients
     */
    @GetMapping("/search")
    public List<Patient> searchPatients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) LocalDate birthDate,
            @RequestParam(required = false) Patient.Gender gender
    ) {
        return patientService.searchPatients(name, firstName, birthDate, gender);
    }

    /**
     * Retrieves a specific patient by its unique identifier
     * @param id Integer representing the patient's identifier
     * @return the founded patient
     */
    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable Integer id) {
        return patientService.getPatientById(id);
    }

    /**
     * Creates a new patient
     * @param patient a Patient to create
     * @return the created patient with the HTTP status
     */
    @PostMapping
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        Patient savedPatient = patientService.savePatient(patient);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    /**
     * Updates an existing patient
     * @param id the patient identifier to be updated
     * @param patient the updated patient data
     * @return the updated patient and the HTTP status
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Integer id, @RequestBody Patient patient) {
        Patient updatedPatient = patientService.updatePatient(id, patient);
        return ResponseEntity.ok(updatedPatient);
    }
}