package com.medilabo.assessment_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GatewayClient {
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

    public <T> T get(String path, Class<T> clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(gatewayUsername, gatewayPassword);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<T> response = restTemplate.exchange(gatewayUrl + path, HttpMethod.GET, entity, clazz);
        return response.getBody();
    }
}