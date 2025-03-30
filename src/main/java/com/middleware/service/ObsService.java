package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.ObsDTO;
import com.middleware.repository.ObsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObsService {
    private final OpenMRSClient openMRSClient;
    private final ObsRepository obsRepository;

    /**
     * Date format used by OpenMRS (with offset).
     */
    private static final DateTimeFormatter OPENMRS_DATE_WITH_OFFSET =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * Saves a list of ObsDTOs to the database.
     * @param obses List of observations to be saved.
     */
    public void saveObsToDatabase(List<ObsDTO> obses) {
        log.info("Obs in die Datenbank speichern beginnt");

        for (ObsDTO obs : obses) {
            if (obs.getUuid() == null) {
                throw new IllegalArgumentException("UUID darf nicht null sein");
            }

            try {
                obsRepository.saveObs(obs);
                log.info("✅ Obs erfolgreich in der Datenbank gespeichert: {}", obs.getUuid());
            } catch (DataAccessException e) {
                log.error("❌ Fehler beim Speichern des Obs in die Datenbank {}: {}", obs.getUuid(), e.getMessage(), e);
                throw new IllegalArgumentException("Fehler beim Speichern des Obs", e);
            }
        }

        log.info("Obs in die Datenbank speichern beendet");
    }

    /**
     * Retrieves all observations for a patient based on their UUID from OpenMRS.
     * @param patientUUID UUID of the patient.
     * @return List of observations.
     */
    public List<ObsDTO> getObsByPatientUUID(UUID patientUUID) {
        log.info("Datenabruf der Obs für Patient {} beginnt", patientUUID);
        List<ObsDTO> obsList = new ArrayList<>();
        String nextUrl = "obs?patient=" + patientUUID + "&limit=1&startIndex=0&v=full";

        try {
            while (nextUrl != null) {
                JsonNode body = openMRSClient.getForEndpoint(nextUrl);
                if (body != null && body.has("results")) {
                    for (JsonNode obs : body.get("results")) {

                        JsonNode conceptNode = obs.path("concept");
                        JsonNode valueNode = obs.path("value");

                        obsList.add(new ObsDTO(
                                UUID.fromString(obs.path("uuid").asText()),
                                obs.path("display").asText(),
                                patientUUID,
                                obs.has("obsDatetime") ? OffsetDateTime.parse(obs.path("obsDatetime").asText(), OPENMRS_DATE_WITH_OFFSET) : null,
                                conceptNode.has("uuid") ? UUID.fromString(conceptNode.path("uuid").asText()) : null,
                                conceptNode.path("name").path("name").asText(null),
                                valueNode.has("uuid") ? UUID.fromString(valueNode.path("uuid").asText()) : null
                        ));
                    }
                }

                nextUrl = null;
                if (body != null && body.has("links")) {
                    for (JsonNode link : body.get("links")) {
                        if (link.has("rel") && "next".equals(link.get("rel").asText())) {
                            nextUrl = link.get("uri").asText().replace(OpenMRSClient.BASE_URL, "");
                            break;
                        }
                    }
                }
            }

            log.info("✅ Obs erfolgreich von OpenMRS in die Middleware geladen für Patient {}", patientUUID);
        } catch (Exception e) {
            log.error("❌ Fehler beim Laden der Obs von OpenMRS für Patient {}: {}", patientUUID, e.getMessage(), e);
        }

        return obsList;
    }

    /**
     * Retrieves all observations for a list of patients.
     * @param patientUUIDs List of patient UUIDs.
     * @return List of all observations.
     */
    public List<ObsDTO> getAllObsForAllPatients(List<UUID> patientUUIDs) {
        log.info("Datenabruf der Obs für alle Patienten beginnt");
        List<ObsDTO> allObs = new ArrayList<>();

        try {
            for (UUID uuid : patientUUIDs) {
                allObs.addAll(getObsByPatientUUID(uuid));
            }

            log.info("✅ Alle Obs erfolgreich für alle Patienten geladen.");
        } catch (Exception e) {
            log.error("❌ Fehler beim Abrufen der Obs für alle Patienten: {}", e.getMessage(), e);
        }

        return allObs;
    }

}
