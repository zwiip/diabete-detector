package com.medilabo.patientservice.services;

import com.medilabo.patientservice.entities.Patient;
import com.medilabo.patientservice.repositories.PatientRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Integer id) {
        return patientRepository.findById(id);
    }

    public List<Patient> searchPatients(String name, String firstName, LocalDate birthDate, Patient.Gender gender) {
        return patientRepository.findAll().stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> firstName == null || p.getFirstName().toLowerCase().contains(firstName.toLowerCase()))
                .filter(p -> birthDate == null || p.getBirthDate().equals(birthDate))
                .filter(p -> gender == null || p.getGender() == gender)
                .toList();
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public void deletePatient(Integer id) {
        if (!patientRepository.existsById(id)) {
            throw new EmptyResultDataAccessException(1);
        }
        patientRepository.deleteById(id);
    }
}
