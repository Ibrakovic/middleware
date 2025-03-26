package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.PatientDTO;
import com.middleware.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final OpenMRSClient openMRSClient;
    private final PatientRepository patientRepository;

    /**
     * Gets all patients from OpenMRS.
     * @return List of PatientDTO objects representing the patients.
     */
    public List<PatientDTO> getAllPatients() {
        JsonNode allPatients = fetchPatients();
        List<PatientDTO> patientDTOs = new ArrayList<>();
        for (JsonNode patient : allPatients) {
            patientDTOs.add(mapJsonToPatientDTO(patient));
        }
        return patientDTOs;
    }

    /**
     * Saves a list of patients to the database.
     * @param patients List of patients to save.
     */
    public void savePatientToDatabase(List<PatientDTO> patients) {
        log.info("Patients speichern beginnt");
        for (PatientDTO patient : patients) {
            String result = patientRepository.savePatient(patient);
            System.out.println(result);
        }
        log.info("Patients speichern beendet");
    }
    /**
     * Maps JSON data of a patient from OpenMRS to a PatientDTO object.
     * @param patientJson JSON-Data of a patient from OpenMRS.
     * @return PatientDTO object representing the patient.
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
     * Extracts the UUIDs of the patients from a list of PatientDTOs.
     * @param patients PatientDTO objects from which to extract the UUIDs.
     * @return List of UUIDs of the patients.
     */
    public static List<UUID> getPatientUUIDs( List<PatientDTO> patients) {
        List<UUID> patientUUIDs = new ArrayList<>();
        for (PatientDTO patient : patients) {
            patientUUIDs.add(patient.getUuid());
        }
        return patientUUIDs;
    }


    /**
     * Fetches all patients from OpenMRS.
     * @return JSON array of all patients.
     */
    public JsonNode fetchPatients() {
        String nextUrl = "patient?q=all&limit=1&v=default";
        ArrayNode allPatients = openMRSClient.getObjectMapper().createArrayNode();

        while (nextUrl != null) { // as long as there are more pages of patients to fetch
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
