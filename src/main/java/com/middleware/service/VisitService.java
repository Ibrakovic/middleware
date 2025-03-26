package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDateTime;
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

    public void saveVisitsToDatabase(List<VisitDTO> visits) {

        log.info("Besuche speichern beginnt");
        for (VisitDTO visit : visits) {
            String result = visitRepository.saveVisit(visit);
            System.out.println(result);
        }
        log.info("Besuche speichern beendet");
    }

    /**
     * Get all visits from OpenMRS
     * @return List of VisitDTO
     */
    public List<VisitDTO> getVisitsFromLastHour() {
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
        List<VisitDTO> visits = new ArrayList<>();

        String nextEndpoint = endpoint;

        // If there is a next endpoint, keep fetching data
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
                            System.err.println("Fehler beim Decodieren der URL: " + e.getMessage());
                        }

                        // If the next URL is a full URL, extract the endpoint
                        if (nextUrl.startsWith(OpenMRSClient.BASE_URL)) {
                            nextEndpoint = nextUrl.substring(OpenMRSClient.BASE_URL.length());
                            System.out.println("Debug2: " + nextEndpoint);
                        } else {
                            nextEndpoint = nextUrl;
                        }

                        // Double slashes are not allowed in URLs
                        if (nextEndpoint.startsWith("//")) {
                            nextEndpoint = nextEndpoint.substring(1);
                        }

                        break;
                    }
                }
            }
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
        // Grundlegende Felder mappen
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

        // Patient mapping
        JsonNode patientNode = node.path("patient");
        if (!patientNode.isMissingNode()) {
            dto.setPatientUUID(UUID.fromString(patientNode.path("uuid").asText()));
            dto.setPatientDisplay(patientNode.path("display").asText());
        }

        // VisitType mapping
        JsonNode visitTypeNode = node.path("visitType");
        if (!visitTypeNode.isMissingNode()) {
            dto.setVisitTypeUUID(UUID.fromString(visitTypeNode.path("uuid").asText()));
            dto.setVisitTypeDisplay(visitTypeNode.path("display").asText());
        }

        // Location mapping
        JsonNode locationNode = node.path("location");
        if (!locationNode.isMissingNode()) {
            dto.setVisitLocationUUID(UUID.fromString(locationNode.path("uuid").asText()));
            dto.setVisitLocationDisplay(locationNode.path("display").asText());
        }

        // Encounters mapping
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
