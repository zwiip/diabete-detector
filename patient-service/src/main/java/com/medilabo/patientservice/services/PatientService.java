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

    /**
     * Retrieves all patients in the database.
     * @return a list of Patient object.
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Retrieves a Patient according to its Identifier.
     * @param id, Integer : the patient identifier we want to retrieve.
     * @return an Optional containing the Patient if found
     *         empty Optional otherwise
     */
    public Optional<Patient> getPatientById(Integer id) {
        return patientRepository.findById(id);
    }

    /**
     * Retrieves patients matching given filtering criteria.
     * All parameters are optional. If a parameter is null, it's ignored.
     * @param name, a String representing the patient's name, (partial match, case-insensitive),
     * @param firstName, a String representing the patient's firstname, (partial match, case-insensitive),
     * @param birthDate, a LocalDate representing the patient's birthdate,
     * @param gender, a Patient.Gender enum option (MALE, FEMALE or OTHER).
     * @return a list of Patients matching the provided criteria.
     */
    public List<Patient> searchPatients(String name, String firstName, LocalDate birthDate, Patient.Gender gender) {
        return patientRepository.findAll().stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> firstName == null || p.getFirstName().toLowerCase().contains(firstName.toLowerCase()))
                .filter(p -> birthDate == null || p.getBirthDate().equals(birthDate))
                .filter(p -> gender == null || p.getGender() == gender)
                .toList();
    }

    /**
     * Add a new Patient to database.
     * @param patient, the Patient object to save.
     * @return the persisted patient entity.
     */
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Deletes a patient by its identifier.
     * @param id, Integer : the patient identifier we want to delete.
     * @throws EmptyResultDataAccessException if no patient exists with the given id. The (1) indicates how many lines we expected to impact.
     */
    public void deletePatient(Integer id) {
        if (!patientRepository.existsById(id)) {
            throw new EmptyResultDataAccessException(1);
        }
        patientRepository.deleteById(id);
    }
}
