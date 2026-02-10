package com.medilabo.front_service.client;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client responsible for calling the gateway and forwarding authentication headers.
 */
@Service
public class GatewayClient {
    private static final Logger log = LoggerFactory.getLogger(GatewayClient.class);
    @Value("${gateway.url}")
    private String gatewayUrl;

    private final RestTemplate restTemplate;

    public GatewayClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Performs a GET request to the gateway
     * @param request The HTTP request object containing authentication headers
     * @param path the path called, to append to the gateway url to perform the get action
     * @param responseType the expected response type
     * @param <T> the type of the returned object
     * @return tje body of the response from the gateway, or null if empty
     * @throws RestClientException if the GET request fails
     */
    public <T> T get(HttpServletRequest request, String path, Class<T> responseType) {
        String url = gatewayUrl + path;
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders(request));

        try {
            log.debug("GET {}", url);

            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    responseType
            );

            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error calling gateway GET {}", url, e);
            throw e;
        }
    }

    /**
     * Performs a POST request to the gateway with a request body, forwarding any cookies.
     * @param request the incoming HTTP request containing authentication headers
     * @param path the path to append to the gateway base URL
     * @param body the object to send as the request body
     * @throws RestClientException if the POST request fails
     */
    public void post(HttpServletRequest request, String path, Object body) {
        executeWithBody(request, path, body, HttpMethod.POST);
    }

    /**
     * Performs a PUT request to the gateway with a request body, forwarding any cookies.
     * @param request the incoming HTTP request containing authentication headers
     * @param path the path to append to the gateway base URL
     * @param body the object to send as the request body
     * @throws RestClientException if the PUT request fails
     */
    public void put(HttpServletRequest request, String path, Object body) {
        executeWithBody(request, path, body, HttpMethod.PUT);
    }


    /**
     * Executes a POST or PUT request with a request body and centralized logging/error handling.
     * @param request the incoming HTTP request containing authentication headers
     * @param path the path to append to the gateway base URL
     * @param body the object to send as the request body
     * @param method the HTTP method (POST or PUT)
     * @throws RestClientException if the request fails
     */
    private void executeWithBody(HttpServletRequest request, String path, Object body, HttpMethod method) {
        String url = gatewayUrl + path;
        HttpEntity<Object> entity = new HttpEntity<>(body, buildHeaders(request));

        try {
            log.debug("{} {}", method, url);
            restTemplate.exchange(url, method, entity, Void.class);

        } catch (RestClientException e) {
            log.error("Error calling gateway {} {}", method, url, e);
            throw e;
        }
    }

    /**
     * Builds HTTP headers for gateway calls by propagating cookies from the incoming request.
     * @param request the incoming HTTP request
     * @return HttpHeaders containing the cookies, if present
     */
    private HttpHeaders buildHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        String cookie = request.getHeader(HttpHeaders.COOKIE);
        if (cookie != null) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }
        return headers;
    }

}
