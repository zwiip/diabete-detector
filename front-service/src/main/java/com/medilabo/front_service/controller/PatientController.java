package com.medilabo.front_service.controller;

import com.medilabo.front_service.client.GatewayClient;
import com.medilabo.front_service.dto.AssessmentDTO;
import com.medilabo.front_service.dto.NoteCreateDTO;
import com.medilabo.front_service.dto.NoteDTO;
import com.medilabo.front_service.dto.PatientDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class PatientController {

    private final GatewayClient gatewayClient;
    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    public PatientController(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    /**
     * Display the list of patients which can be filtered on given optional criteria.
     * Adds the resulting list of patients to the model for display in the "patients" view.
     * @param name a String representing the name of the given patient
     * @param firstName a String representing the first name of the given patient
     * @param birthDate a LocalDate object representing the date of birth of the given patient
     * @param gender a String representing the gender of the patient
     * @param request the HTTP request object
     * @param model the model to which attributes are added to be used in the view
     * @return the name of the view "patients" to display the list of patient
     */
    @GetMapping("/patients/search")
    public String listPatients(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String firstName,
                               @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                   LocalDate birthDate,
                               @RequestParam(required = false) String gender,
                               HttpServletRequest request,
                               Model model) {
        log.debug("Searching for patients : name= {}, firstname= {}, birthDate= {}, gender= {}", name, firstName, birthDate, gender);

        String path = UriComponentsBuilder.fromPath("/patients/search")
                .queryParamIfPresent("name", Optional.ofNullable(name))
                .queryParamIfPresent("firstName", Optional.ofNullable(firstName))
                .queryParamIfPresent("birthDate", Optional.ofNullable(birthDate))
                .queryParamIfPresent("gender", Optional.ofNullable(gender))
                .toUriString();

        PatientDTO[] response = new PatientDTO[0];

        try {
            response = gatewayClient.get(request, path, PatientDTO[].class);
        } catch (RestClientException e) {
            log.error("Error fetching the patients", e);
            model.addAttribute("errorMessage", "Impossible de récupérer les patients pour le moment.");
        }

        List<PatientDTO> patients = Arrays.asList(response);

        if (patients.isEmpty()) {
            log.warn("No patients matching given criteria.");
        } else {
            log.info("{} found patients.", patients.size());
        }

        model.addAttribute("activePage", "patients/search");
        model.addAttribute("patients", patients);
        model.addAttribute("name", name);
        model.addAttribute("firstName", firstName);
        model.addAttribute("birthDate", birthDate);
        model.addAttribute("gender", gender);
        return "patients";
    }

    /**
     * Display the form to add a new patient
     * @return the name of the view to add a new patient
     */
    @GetMapping("patients/new")
    public String addPatientForm() {
        return "new";
    }

    /**
     * Adding a new patient.
     * @param patient a PatientDTO object to save to the database
     * @param request the HTTP request object
     * @return the name of the redirection view (the list of patients) in case of success
     *          the name of the form to add a new patient (new) in case of error
     */
    @PostMapping("/patients")
    public String createPatient(@ModelAttribute PatientDTO patient, HttpServletRequest request, Model model) {
        try {
            gatewayClient.post(request, "/patients", patient);
            log.info("New patient created: {} {}", patient.getName(), patient.getFirstName());
        } catch (RestClientException e) {
            log.error("Error creating the patient", e);
            model.addAttribute("errorMessage", "Impossible de créer le patient pour le moment.");
            return "new";
        }
        return "redirect:/patients/search";
    }

    /**
     * Add a note to a patient
     * @param newNote a NodeCreateDTO object representing the note to save to database
     * @param request the HTTP request object
     * @return the name of the redirection view (the patient's details page)
     */
    @PostMapping("/notes")
    public String createNote(@ModelAttribute NoteCreateDTO newNote, HttpServletRequest request, Model model) {
        try {
            gatewayClient.post(request, "/notes", newNote);
            log.info("New note created for patientId={}", newNote.getPatientId());
        } catch (RestClientException e) {
            log.error("Error creating note for patientId={}", newNote.getPatientId(), e);
            model.addAttribute("errorMessage", "Impossible d'ajouter la note.");
            return "patient-details";
        }

        return "redirect:/patients/" + newNote.getPatientId();
    }

    /**
     * Display a patient's details page matching the given id.
     * The details of a patient are composed of its information from Patient-service, its notes from note-service, and its assessments from assessments-service.
     * @param id an Integer representing the patient's identifier
     * @param request the HTTP request object
     * @param model the model to which attributes are added to be used in the view
     * @return the name of the view to display (patient-details) with the patient's information.
     */
    @GetMapping("/patients/{id}")
    public String patientDetails(@PathVariable Integer id, HttpServletRequest request, Model model) {
        PatientDTO patient = null;
        try {
            patient = gatewayClient.get(request, "/patients/" + id, PatientDTO.class);
            if (patient == null) {
                log.warn("Patient not found: id={}", id);
                model.addAttribute("errorMessage", "Patient non trouvé.");
                return "patient-details";
            }
        } catch (RestClientException e) {
            log.error("Error fetching patient for id={}", id, e);
            model.addAttribute("errorMessage", "Impossible de récupérer les informations du patient.");
        }

        List<NoteDTO> notes = List.of();
        try {
            notes = Arrays.asList(gatewayClient.get(request, "/notes/" + id, NoteDTO[].class));
        } catch (RestClientException e) {
            log.error("Error fetching notes for patient id={}", id, e);
            model.addAttribute("notesError", "Impossible de récupérer les notes du patient.");
        }

        AssessmentDTO assessment = null;
        try {
            assessment = gatewayClient.get(request, "/assessment/" + id, AssessmentDTO.class);
        } catch (RestClientException e) {
            log.error("Error fetching assessment for patient id={}", id, e);
            model.addAttribute("assessmentError", "Impossible de récupérer l'évaluation médicale.");
        }

        NoteCreateDTO newNote = new NoteCreateDTO();
        newNote.setPatientId(id);

        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes);
        model.addAttribute("newNote", newNote);
        model.addAttribute("assessment", assessment);

        return "patient-details";
    }

    /**
     * Update the patient's information
     * @param id the Integer of the patient's identifier
     * @param patient a PatientDTO object with new information to save to the database.
     * @param request the HTTP request object
     * @return the name of the redirection view (the patient's details page with updated information).
     */
    @PostMapping("/patients/{id}")
    public String updatePatient(@PathVariable Integer id, @ModelAttribute PatientDTO patient, HttpServletRequest request, Model model) {
        try {
            gatewayClient.put(request, "/patients/" + id, patient);
            log.info("Patient updated: id={}, Name= {}", id, patient.getName());
        } catch (RestClientException e) {
            log.error("Error updating patient: id={}, Name ={}", id, patient.getName(), e);
            model.addAttribute("errorMessage", "Impossible de mettre à jour le patient pour le moment.");
            return "patient-details";
        }

        return "redirect:/patients/" + id;
    }
}

