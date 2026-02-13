package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.exceptions.ExternalServiceException;
import com.medilabo.assessment_service.exceptions.PatientNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Generic client for calling the gateway service
 */
@Service
public class GatewayClient {
    private static final Logger log = LoggerFactory.getLogger(GatewayClient.class);

    @Value("${gateway.username}")
    private String gatewayUsername;

    @Value("${gateway.password}")
    private String gatewayPassword;

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final RestTemplate restTemplate;

    public GatewayClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     *
     * @param path relative URL to call
     * @param clazz expected response type
     * @return the response object
     * @param <T> generic type for the object returned
     * @throws PatientNotFoundException if the patient is not found
     * @throws ExternalServiceException for other gateway failures
     */
    public <T> T get(String path, Class<T> clazz, Integer patientId) {
        log.debug("Calling gateway path={} for patientId={}", path, patientId);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(gatewayUsername, gatewayPassword);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<T> response = restTemplate.exchange(gatewayUrl + path, HttpMethod.GET, entity, clazz);

            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Patient not found, id={}", patientId);
            throw new PatientNotFoundException(patientId);
        } catch (Exception e) {
            log.error("Gateway call failed for path={} patientId={}, cause={}", path, patientId, e.getMessage());
            throw new ExternalServiceException("Gateway call failed for path: " + path, e);
        }
    }
}