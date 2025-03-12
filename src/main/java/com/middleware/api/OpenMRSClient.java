package com.middleware.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenMRSClient {

    private static final String BASE_URL = "http://localhost:8081/openmrs-standalone/ws/rest/v1/";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "test";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenMRSClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode getAllPatients() {
        String nextUrl = BASE_URL + "patient?q=all&limit=1&v=default";
        ArrayNode allPatients = objectMapper.createArrayNode();

        // Erstelle Header und setze die Basic-Auth
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(USERNAME, PASSWORD);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        while (nextUrl != null) {
            ResponseEntity<JsonNode> response = restTemplate.exchange(nextUrl, HttpMethod.GET, entity, JsonNode.class);
            JsonNode body = response.getBody();
            if (body == null) {
                break;
            }

            JsonNode results = body.get("results");
            if (results != null && results.isArray()) {
                for (JsonNode patient : results) {
                    allPatients.add(patient);
                }
            }

            nextUrl = null;
            JsonNode links = body.get("links");
            if (links != null && links.isArray()) {
                for (JsonNode linkObj : links) {
                    if (linkObj.has("rel") && "next".equals(linkObj.get("rel").asText())) {
                        nextUrl = linkObj.get("uri").asText();
                        break;
                    }
                }
            }
        }

        return allPatients;
    }
}
