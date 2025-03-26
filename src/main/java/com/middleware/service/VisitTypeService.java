package com.middleware.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.VisitTypeDTO;
import com.middleware.repository.VisitTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitTypeService {

    private final OpenMRSClient openMRSClient;
    private final VisitTypeRepository visitTypeRepository;

    public void saveVisitTypesToDatabase(List<VisitTypeDTO> visitTypes) {

        log.info("VisitType speichern beginnt");
        for (VisitTypeDTO visitType : visitTypes) {
            String result = visitTypeRepository.saveVisitType(visitType);
            System.out.println(result);
        }
        log.info("VisitType speichern beendet");
    }

    /**
     * Get all visit types from OpenMRS
     * @return List of VisitTypeDTO
     */
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
