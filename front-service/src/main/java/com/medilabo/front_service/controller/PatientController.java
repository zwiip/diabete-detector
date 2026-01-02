package com.medilabo.front_service.controller;

import com.medilabo.front_service.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class PatientController {

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/patients")
    public String listPatients(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String firstName,
                               @RequestParam(required = false) LocalDate birthDate,
                               @RequestParam(required = false) String gender,
                               Model model) {


        String url = UriComponentsBuilder.fromHttpUrl(gatewayUrl  + "/patients")
            .queryParamIfPresent("name", Optional.ofNullable(name))
            .queryParamIfPresent("firstName", Optional.ofNullable(firstName))
            .queryParamIfPresent("birthDate", Optional.ofNullable(birthDate))
            .queryParamIfPresent("gender", Optional.ofNullable(gender))
            .toUriString();

        List<PatientDTO> patients = Arrays.asList(
                restTemplate.getForObject(url, PatientDTO[].class)
        );

        model.addAttribute("patients", patients);
        model.addAttribute("name", name);
        model.addAttribute("firstName", firstName);
        model.addAttribute("birthDate", birthDate);
        model.addAttribute("gender", gender);
        return "patients";
    }
}

