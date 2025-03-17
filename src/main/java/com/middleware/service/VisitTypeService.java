package com.middleware.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.VisitTypeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitTypeService {

    private final OpenMRSClient openMRSClient;

    public List<VisitTypeDTO> getAllVisitTypes() {
        String endpoint = "visittype?v=full";
        JsonNode body = openMRSClient.getForEndpoint(endpoint);
        List<VisitTypeDTO> visitTypes = new ArrayList<>();

        if (body != null && body.has("results")) {
            for (JsonNode visitType : body.get("results")) {
                visitTypes.add(new VisitTypeDTO(
                        UUID.fromString(visitType.path("uuid").asText()),
                        visitType.path("name").asText(),
                        visitType.path("description").asText(null),
                        visitType.path("retired").asBoolean(false)
                ));
            }
        }

        return visitTypes;
    }
}
