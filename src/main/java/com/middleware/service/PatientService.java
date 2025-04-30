package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.PatientDTO;
import com.middleware.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        List<PatientDTO> patientDTOs = new ArrayList<>();
        try {
            JsonNode allPatients = fetchPatients();
            for (JsonNode patient : allPatients) {
                patientDTOs.add(mapJsonToPatientDTO(patient));
            }
            log.info("Patients erfolgreich von OpenMRS in die Middleware geladen.");
        } catch (Exception e) {
            log.error("Fehler beim Abrufen der Patients von OpenMRS: {}", e.getMessage(), e);
        }
        return patientDTOs;
    }

    /**
     * Saves a list of patients to the database.
     * @param patients List of patients to save.
     */
    public void savePatientToDatabase(List<PatientDTO> patients) {
        log.info("Patients in die Datenbank speichern beginnt");
        for (PatientDTO patient : patients) {
            if(patient.getUuid() == null) {
                throw new IllegalArgumentException("UUID darf nicht null sein");
            }
            try {
                String result = patientRepository.savePatient(patient);
                log.info(result);
            } catch (Exception e) {
                log.error("Fehler beim Speichern des Patients in die Datenbank {}: {}", patient.getUuid(), e.getMessage(), e);
                throw new IllegalArgumentException("Fehler beim Speichern des Patienten", e);
            }
        }
        log.info("Patients in die Datenbank speichern beendet");
    }

    /**
     * Maps JSON data of a patient from OpenMRS to a PatientDTO object.
     * @param patientJson JSON-Data of a patient from OpenMRS.
     * @return PatientDTO object representing the patient.
     */
    private PatientDTO mapJsonToPatientDTO(JsonNode patientJson) {
        try {
            UUID uuid = UUID.fromString(patientJson.path("uuid").asText());

            String fullDisplay = patientJson.path("display").asText();
            String identifier = extractIdentifier(fullDisplay);

            JsonNode personNode = patientJson.path("person");
            String personName = personNode.path("display").asText();
            String gender = personNode.path("gender").asText();
            int age = personNode.path("age").asInt();
            String birthdate = personNode.path("birthdate").asText();

            return new PatientDTO(uuid, identifier, personName, gender, age, birthdate);
        } catch (Exception e) {
            log.error("Fehler beim Umwandeln eines Patienten-JSON in PatientDTO: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extracts the identifier from the display string. The display string has the format "identifier - name".
     * @param display The display string of the patient from OpenMRS.
     * @return The identifier of the patient. Returns an empty string if no identifier is found.
     */
    private String extractIdentifier(String display) {
        try {
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
        } catch (Exception e) {
            log.error("Fehler beim Extrahieren des Identifiers aus dem Display-String: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Extracts the UUIDs of the patients from a list of PatientDTOs.
     * @param patients PatientDTO objects from which to extract the UUIDs.
     * @return List of UUIDs of the patients.
     */
    public static List<UUID> getPatientUUIDs(List<PatientDTO> patients) {
        List<UUID> patientUUIDs = new ArrayList<>();
        try {
            for (PatientDTO patient : patients) {
                patientUUIDs.add(patient.getUuid());
            }
        } catch (Exception e) {
            log.error("Fehler beim Extrahieren der UUIDs aus der Patientenliste: {}", e.getMessage(), e);
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

        try {
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
        } catch (Exception e) {
            log.error("Fehler beim Abrufen der Patienten von OpenMRS: {}", e.getMessage(), e);
        }

        return allPatients;
    }
}
