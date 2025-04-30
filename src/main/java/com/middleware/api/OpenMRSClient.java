package com.middleware.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Data
public class OpenMRSClient {

    public static final String BASE_URL = "http://localhost:8082/openmrs-standalone/ws/rest/v1/";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "test";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenMRSClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }



    /**
     * Prepairs the URL for the endpoint and sends a GET request to the OpenMRS API.
     * @param endpoint The endpoint to send the GET request to.
     * @Retrayable  Retries the request in case of failure of connection(e.g. server down, internet down). 3 attempts with 5 minutes delay.
     * @return The response from the OpenMRS API.
     */
    @Retryable(
            value = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 minutes
    )
    public JsonNode getForEndpoint(String endpoint) {
        String url = BASE_URL + endpoint;
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(USERNAME, PASSWORD);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody();
    }

    @Recover
    public JsonNode recover(Exception e, String endpoint) {
        System.out.println("Fehler beim Abrufen der Daten von OpenMRS: " + e.getMessage());
        return null;
    }
}
