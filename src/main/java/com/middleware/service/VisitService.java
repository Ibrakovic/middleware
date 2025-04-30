package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.VisitDTO;
import com.middleware.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitService {

    private final OpenMRSClient openMRSClient;
    private final VisitRepository visitRepository;

    public void saveVisitToDatabase(List<VisitDTO> visits) {
        log.info("Visits speichern beginnt");

        for (VisitDTO visit : visits) {
            if (visit.getUuid() == null) {
                throw new IllegalArgumentException("UUID darf nicht null sein");
            }

            try {
                String result = visitRepository.saveVisit(visit);
                log.info(result);
            } catch (IllegalArgumentException e) {
                log.error("Fehler beim Speichern des Visit in die Datenbank {}: {}", visit.getUuid(), e.getMessage(), e);
                throw new IllegalArgumentException("Fehler beim Speichern des Visit", e);
            }
        }

        log.info("Visits speichern beendet");
    }

    /**
     * Get all visits from OpenMRS
     * @return List of VisitDTO
     */
    public List<VisitDTO> getVisitsFromLastHour() {
        List<VisitDTO> visits = new ArrayList<>();
        try {
            Instant oneHourAgo = Instant.now().minusSeconds(7200);
            String fromStartDate = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .withZone(ZoneOffset.UTC)
                    .format(oneHourAgo);

            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("visit")
                    .queryParam("includeInactive", true)
                    .queryParam("fromStartDate", fromStartDate)
                    .queryParam("v", "default")
                    .queryParam("limit", 1)
                    .queryParam("startIndex", 0)
                    .encode(StandardCharsets.UTF_8);

            String endpoint = builder.toUriString();
            String nextEndpoint = endpoint;

            while (nextEndpoint != null) {
                JsonNode response = openMRSClient.getForEndpoint(nextEndpoint);
                JsonNode results = response.path("results");
                if (results.isArray()) {
                    for (JsonNode node : results) {
                        visits.add(mapJsonToVisitDTO(node));
                    }
                }

                nextEndpoint = null;
                JsonNode links = response.path("links");
                if (links.isArray()) {
                    for (JsonNode link : links) {
                        if ("next".equals(link.path("rel").asText())) {
                            String nextUrl = link.path("uri").asText();
                            try {
                                nextUrl = URLDecoder.decode(nextUrl, StandardCharsets.UTF_8);
                            } catch (Exception e) {
                                log.error("Fehler beim Decodieren der URL: {}", e.getMessage(), e);
                            }

                            if (nextUrl.startsWith(OpenMRSClient.BASE_URL)) {
                                nextEndpoint = nextUrl.substring(OpenMRSClient.BASE_URL.length());
                                log.debug("Nächste Seite: {}", nextEndpoint);
                            } else {
                                nextEndpoint = nextUrl;
                            }

                            if (nextEndpoint.startsWith("//")) {
                                nextEndpoint = nextEndpoint.substring(1);
                            }

                            break;
                        }
                    }
                }
            }

            log.info("Visit erfolgreich von OpenMRS in die Middleware geladen.");
        } catch (Exception e) {
            log.error("Fehler beim Laden der Visit von OpenMRS: {}", e.getMessage(), e);
        }

        return visits;
    }

    /**
     * Maps a JSON node to a VisitDTO object.
     * @param node JSON node
     * @return VisitDTO object
     */
    private VisitDTO mapJsonToVisitDTO(JsonNode node) {
        VisitDTO dto = new VisitDTO();
        dto.setUuid(UUID.fromString(node.path("uuid").asText()));
        dto.setDisplay(node.path("display").asText());

        String start = node.path("startDatetime").asText(null);
        String stop = node.path("stopDatetime").asText(null);

        dto.setStartDatetime(
                (start != null && !start.equals("null") && !start.isEmpty())
                        ? OffsetDateTime.parse(start, OPENMRS_DATE_WITH_OFFSET)
                        : null
        );

        dto.setStopDatetime(
                (stop != null && !stop.equals("null") && !stop.isEmpty())
                        ? OffsetDateTime.parse(stop, OPENMRS_DATE_WITH_OFFSET)
                        : null
        );

        JsonNode patientNode = node.path("patient");
        if (!patientNode.isMissingNode()) {
            dto.setPatientUUID(UUID.fromString(patientNode.path("uuid").asText()));
            dto.setPatientDisplay(patientNode.path("display").asText());
        }

        JsonNode visitTypeNode = node.path("visitType");
        if (!visitTypeNode.isMissingNode()) {
            dto.setVisitTypeUUID(UUID.fromString(visitTypeNode.path("uuid").asText()));
            dto.setVisitTypeDisplay(visitTypeNode.path("display").asText());
        }

        JsonNode locationNode = node.path("location");
        if (!locationNode.isMissingNode()) {
            dto.setVisitLocationUUID(UUID.fromString(locationNode.path("uuid").asText()));
            dto.setVisitLocationDisplay(locationNode.path("display").asText());
        }

        List<UUID> encounterUUIDs = new ArrayList<>();
        String encounterDisplay = "";
        JsonNode encountersNode = node.path("encounters");
        if (encountersNode.isArray() && !encountersNode.isEmpty()) {
            for (JsonNode enc : encountersNode) {
                String encUUID = enc.path("uuid").asText();
                if (!encUUID.isEmpty()) {
                    encounterUUIDs.add(UUID.fromString(encUUID));
                }
            }
            encounterDisplay = encountersNode.get(0).path("display").asText();
        }

        dto.setEncounterUUID(encounterUUIDs.isEmpty() ? null : encounterUUIDs.get(0));
        dto.setEncounterDisplay(encounterDisplay);

        return dto;
    }

    /**
     * Date format for OpenMRS dates with offset.
     */
    private static final DateTimeFormatter OPENMRS_DATE_WITH_OFFSET =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
}
