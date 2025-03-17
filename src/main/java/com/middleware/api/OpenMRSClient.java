package com.middleware.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenMRSClient {

    public static final String BASE_URL = "http://localhost:8082/openmrs-standalone/ws/rest/v1/";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "test";

    private final RestTemplate restTemplate;
    @Getter
    private final ObjectMapper objectMapper;

    public OpenMRSClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }


    /**
     * Holt die Daten für einen bestimmten Endpunkt von OpenMRS.
     * @param endpoint
     * @return
     */
    public JsonNode getForEndpoint(String endpoint) {
        String url = BASE_URL + endpoint;
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(USERNAME, PASSWORD);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody();
    }

}
