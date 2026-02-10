package com.medilabo.front_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RestClientException.class)
    public String handleGatewayError(RestClientException e, Model model) {
        log.error("Gateway error", e);
        model.addAttribute("errorType", "SERVICE_UNAVAILABLE");
        model.addAttribute("errorMessage", "Le service est momentanément indisponible.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpectedError(Exception e, Model model) {
        log.error("Unexpected error", e);
        model.addAttribute("errorType", "INTERNAL_ERROR");
        model.addAttribute("errorMessage", "Une erreur inattendue est survenue.");
        return "error";
    }
}
