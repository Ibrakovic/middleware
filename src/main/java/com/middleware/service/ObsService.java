package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.ObsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObsService {
    private final OpenMRSClient openMRSClient;

    public List<ObsDTO> getObsByPatientUUID(UUID patientUUID) {
        List<ObsDTO> obsList = new ArrayList<>();
        String nextUrl = "obs?patient=" + patientUUID + "&limit=1&startIndex=0&v=full";

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
                            obs.path("obsDatetime").asText(null),
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

    public List<ObsDTO> getAllObsForAllPatients(List<UUID> patientUUIDs) {
        List<ObsDTO> allObs = new ArrayList<>();

        for (UUID uuid : patientUUIDs) {
            allObs.addAll(getObsByPatientUUID(uuid));
        }

        return allObs;
    }
}
