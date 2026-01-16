package com.medilabo.patientservice.controllers;

import com.medilabo.patientservice.entities.Patient;
import com.medilabo.patientservice.services.PatientService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/search")
    public List<Patient> searchPatients(

            @RequestParam(required = false) String name,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) LocalDate birthDate,
            @RequestParam(required = false) Patient.Gender gender
    ) {
        return patientService.searchPatients(name, firstName, birthDate, gender);
    }

    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable Integer id) {
        return patientService.getPatientById(id);
    }

    @PostMapping
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        Patient savedPatient = patientService.savePatient(patient);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Integer id, @RequestBody Patient patient) {
        try {
            patientService.updatePatient(id, patient);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return ResponseEntity.notFound().build();
        }
    }


//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePatient(@PathVariable Integer id) {
//        try {
//            patientService.deletePatient(id);
//            return ResponseEntity.noContent().build();
//        } catch (EmptyResultDataAccessException exception) {
//            return ResponseEntity.notFound().build();
//        }
//    }
}