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

    /**
     * Get all visit types from OpenMRS
     * @param visitTypes List of visit types to save
     */
    public void saveVisitTypeToDatabase(List<VisitTypeDTO> visitTypes) {
        log.info("VisitType in die Datenbank speichern beginnt");

        for (VisitTypeDTO visitType : visitTypes) {
            if (visitType.getUuid() == null) {
                throw new IllegalArgumentException("UUID darf nicht null sein");
            }

            try {
                visitTypeRepository.saveVisitType(visitType);
                log.info("✅ VisitType erfolgreich gespeichert in der Datenbank: {}", visitType.getUuid());
            } catch (IllegalArgumentException e) {
                log.error("❌ Fehler beim Speichern des VisitType in die Datenbank {}: {}", visitType.getUuid(), e.getMessage(), e);
                throw new IllegalArgumentException("Fehler beim Speichern des VisitType", e);
            }
        }

        log.info("VisitType in die Datenbank speichern beendet");
    }

    /**
     * Get all visit types from OpenMRS
     * @return List of VisitTypeDTO
     */
    public List<VisitTypeDTO> getAllVisitTypes() {
        List<VisitTypeDTO> visitTypes = new ArrayList<>();
        String endpoint = "visittype?v=full";

        try {
            JsonNode body = openMRSClient.getForEndpoint(endpoint);

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

            log.info("✅ VisitTypes erfolgreich von OpenMRS in die Middleware geladen.");
        } catch (Exception e) {
            log.error("❌ Fehler beim Laden der VisitTypes von OpenMRS: {}", e.getMessage(), e);
        }

        return visitTypes;
    }
}
