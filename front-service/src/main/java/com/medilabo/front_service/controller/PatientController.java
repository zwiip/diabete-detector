package com.medilabo.front_service.controller;

import com.medilabo.front_service.client.GatewayClient;
import com.medilabo.front_service.dto.AssessmentDTO;
import com.medilabo.front_service.dto.NoteCreateDTO;
import com.medilabo.front_service.dto.NoteDTO;
import com.medilabo.front_service.dto.PatientDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class PatientController {

    private final GatewayClient gatewayClient;

    public PatientController(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @GetMapping("/patients/search")
    public String listPatients(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String firstName,
                               @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                   LocalDate birthDate,
                               @RequestParam(required = false) String gender,
                               HttpServletRequest request,
                               Model model) {

        String path = UriComponentsBuilder.fromPath("/patients/search")
                .queryParamIfPresent("name", Optional.ofNullable(name))
                .queryParamIfPresent("firstName", Optional.ofNullable(firstName))
                .queryParamIfPresent("birthDate", Optional.ofNullable(birthDate))
                .queryParamIfPresent("gender", Optional.ofNullable(gender))
                .toUriString();

        PatientDTO[] response = gatewayClient.get(request, path, PatientDTO[].class);
        List<PatientDTO> patients = Arrays.asList(response);

        model.addAttribute("activePage", "patients/search");
        model.addAttribute("patients", patients);
        model.addAttribute("name", name);
        model.addAttribute("firstName", firstName);
        model.addAttribute("birthDate", birthDate);
        model.addAttribute("gender", gender);
        return "patients";
    }

    @GetMapping("patients/new")
    public String addPatientForm() {
        return "new";
    }

    @PostMapping("/patients")
    public String createPatient(@ModelAttribute PatientDTO patient, HttpServletRequest request) {
        gatewayClient.post(request, "/patients", patient);

        return "redirect:/patients/search";
    }

    @PostMapping("/notes")
    public String createNote(@ModelAttribute NoteCreateDTO newNote,
                             HttpServletRequest request) {

        gatewayClient.post(request, "/notes", newNote);
        return "redirect:/patients/" + newNote.getPatientId();
    }

    @GetMapping("/patients/{id}")
    public String patientDetails(@PathVariable Integer id,
                                 HttpServletRequest request,
                                 Model model) {

        PatientDTO patient =
                gatewayClient.get(request, "/patients/" + id, PatientDTO.class);

        List<NoteDTO> notes = Arrays.asList(
                gatewayClient.get(request, "/notes/" + id, NoteDTO[].class)
        );

        AssessmentDTO assessment =
                gatewayClient.get(request, "/assessment/" + id, AssessmentDTO.class);

        NoteCreateDTO newNote = new NoteCreateDTO();
        newNote.setPatientId(id);

        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes);
        model.addAttribute("newNote", newNote);
        model.addAttribute("assessment", assessment);

        return "patient-details";
    }

    @PostMapping("/patients/{id}")
    public String updatePatient(@PathVariable Integer id,
                                @ModelAttribute PatientDTO patient,
                                HttpServletRequest request) {

        gatewayClient.put(request, "/patients/" + id, patient);
        return "redirect:/patients/" + id;
    }
}

