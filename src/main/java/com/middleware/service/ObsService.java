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
     * Date format used by OpenMRS for dates with offset.
     */
    private static final DateTimeFormatter OPENMRS_DATE_WITH_OFFSET =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * Saves a list of ObsDTO objects to the database.
     * @param obses List of ObsDTO objects to save.
     */
    public void saveObsToDatabase(List<ObsDTO> obses) {
        log.info("Obs speichern beginnt");

        for (ObsDTO obs : obses) {
            if (obs.getUuid() == null) {
                throw new IllegalArgumentException("UUID darf nicht null sein");
            }

            try {
                obsRepository.saveObs(obs);
                log.info("✅ Obs erfolgreich gespeichert: {}", obs.getUuid());
            } catch (DataAccessException e) {
                log.error("❌ Fehler beim Speichern des Obs {}: {}", obs.getUuid(), e.getMessage());
                throw new IllegalArgumentException("Fehler beim Speichern des Obs", e);
            }
        }

        log.info("Obs speichern beendet");
    }


    /**
     * Retrieves all observations for a patient with the given UUID.
     * @param patientUUID UUID of the patient to retrieve observations for.
     * @return List of ObsDTO objects representing the observations.
     */
    public List<ObsDTO> getObsByPatientUUID(UUID patientUUID) {
        List<ObsDTO> obsList = new ArrayList<>();
        String nextUrl = "obs?patient=" + patientUUID + "&limit=1&startIndex=0&v=full";

        while (nextUrl != null) { // as long as there is a next URL to fetch more observations from OpenMRS
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

        return obsList;
    }

    /**
     * Retrieves all observations for all patients with the given UUIDs.
     * @param patientUUIDs List of UUIDs of the patients to retrieve observations for.
     * @return List of ObsDTO objects representing the observations.
     */
    public List<ObsDTO> getAllObsForAllPatients(List<UUID> patientUUIDs) {
        List<ObsDTO> allObs = new ArrayList<>();

        for (UUID uuid : patientUUIDs) {
            allObs.addAll(getObsByPatientUUID(uuid));
        }

        return allObs;
    }
}
