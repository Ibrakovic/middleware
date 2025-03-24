package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.PatientDTO;
import com.middleware.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final OpenMRSClient openMRSClient;
    private final PatientRepository patientRepository;

    /**
     * Holt alle Patienten von OpenMRS und mapped sie auf PatientDTO-Objekte.
     * @return Liste von PatientDTO-Objekten.
     */
    public List<PatientDTO> getAllPatients() {
        JsonNode allPatients = fetchPatients();
        List<PatientDTO> patientDTOs = new ArrayList<>();
        for (JsonNode patient : allPatients) {
            patientDTOs.add(mapJsonToPatientDTO(patient));
        }
        return patientDTOs;
    }

    public void savePatientToDatabase(List<PatientDTO> patients) {
        for (PatientDTO patient : patients) {
            String result = patientRepository.savePatient(patient);
            System.out.println(result);
        }
        System.out.println("✅ Erfolgreich " + patients.size() + " Patienten gespeichert.");
    }
    /**
     * Mapped die JSON-Daten eines Patienten aus OpenMRS auf ein PatientDTO-Objekt.
     * @param patientJson JSON-Daten eines Patienten aus OpenMRS.
     * @return PatientDTO-Objekt mit den gemappten Daten.
     */
    private PatientDTO mapJsonToPatientDTO(JsonNode patientJson) {
        UUID uuid = UUID.fromString(patientJson.path("uuid").asText());

        String fullDisplay = patientJson.path("display").asText();
        String identifier = extractIdentifier(fullDisplay);

        JsonNode personNode = patientJson.path("person");
        String personName = personNode.path("display").asText();
        String gender = personNode.path("gender").asText();
        int age = personNode.path("age").asInt();
        String birthdate = personNode.path("birthdate").asText();

        return new PatientDTO(uuid, identifier, personName, gender, age, birthdate);
    }


    /**
     * Extrahiert die Identifier aus dem display-String. Der display-String hat das Format "identifier - name".
     * @param display Der display-String des Patienten aus OpenMRS.
     * @return Der Identifier des Patienten. Wenn kein Identifier gefunden wurde, wird ein leerer String zurückgegeben.
     */
    private String extractIdentifier(String display) {
        if (display == null) {
            return "";
        }
        int firstDash = display.indexOf("-");
        if (firstDash != -1) {
            int secondDash = display.indexOf("-", firstDash + 1);
            if (secondDash != -1) {
                return display.substring(0, secondDash).trim();
            }
        }
        return display;
    }

    /**
     * Extrahiert die UUIDs der Patienten aus einem JSON-Array.
     * @param patientJson JSON-Array mit Patienten.
     * @return Liste von UUIDs der Patienten.
     */
    public static List<UUID> getPatientUUIDs( JsonNode patientJson) {
        List<UUID> patientUUIDs = new ArrayList<>();
        if (patientJson != null && patientJson.isArray()) {
            for (JsonNode patient : patientJson) {
                UUID uuid = UUID.fromString(patient.path("uuid").asText());
                patientUUIDs.add(uuid);
            }
        }
        return patientUUIDs;
    }


    public JsonNode fetchPatients() {
        String nextUrl = "patient?q=all&limit=1&v=default";
        ArrayNode allPatients = openMRSClient.getObjectMapper().createArrayNode();

        while (nextUrl != null) {
            JsonNode body = openMRSClient.getForEndpoint(nextUrl);
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
                        nextUrl = linkObj.get("uri").asText().replace(OpenMRSClient.BASE_URL, "");
                        break;
                    }
                }
            }
        }

        return allPatients;
    }

}
