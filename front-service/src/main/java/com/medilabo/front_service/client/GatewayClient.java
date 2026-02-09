package com.medilabo.front_service.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GatewayClient {
    @Value("${gateway.url}")
    private String gatewayUrl;

    private final RestTemplate restTemplate;

    public GatewayClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T get(HttpServletRequest request, String path, Class<T> responseType) {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders(request));

        ResponseEntity<T> response = restTemplate.exchange(
                gatewayUrl + path,
                HttpMethod.GET,
                entity,
                responseType
        );

        return response.getBody();
    }

    public void post(HttpServletRequest request, String path, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, buildHeaders(request));
        restTemplate.exchange(gatewayUrl + path, HttpMethod.POST, entity, Void.class);
    }

    public void put(HttpServletRequest request, String path, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, buildHeaders(request));
        restTemplate.exchange(gatewayUrl + path, HttpMethod.PUT, entity, Void.class);
    }

    private HttpHeaders buildHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        String cookie = request.getHeader(HttpHeaders.COOKIE);
        if (cookie != null) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }
        return headers;
    }

}
