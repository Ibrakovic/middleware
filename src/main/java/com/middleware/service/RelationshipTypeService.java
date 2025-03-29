package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.RelationshipTypeDTO;
import com.middleware.repository.RelationshipTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelationshipTypeService {
    private final OpenMRSClient openMRSClient;
    private final RelationshipTypeRepository relationshipTypeRepository;

    public void saveRelationshipTypeToDatabase(List<RelationshipTypeDTO> relationshipTypes) {
        log.info("RelationshipTypes speichern beginnt");

        for (RelationshipTypeDTO relationshipType : relationshipTypes) {
            if (relationshipType.getUuid() == null) {
                throw new IllegalArgumentException("UUID darf nicht null sein");
            }

            try {
                relationshipTypeRepository.saveRelationshipType(relationshipType);
                log.info("✅ RelationshipType erfolgreich gespeichert in der Datenbank: {}", relationshipType.getUuid());
            } catch (IllegalArgumentException e) {
                log.error("❌ Fehler beim Speichern des RelationshipType in die Datenbank {}: {}", relationshipType.getUuid(), e.getMessage(), e);
                throw new IllegalArgumentException("Fehler beim Speichern des RelationshipType", e);
            }
        }

        log.info("RelationshipTypes speichern beendet");
    }

    /**
     * Get all relationship types from OpenMRS
     * @return List of RelationshipTypeDTO
     */
    public List<RelationshipTypeDTO> getAllRelationshipTypes() {
        List<RelationshipTypeDTO> relationshipTypeList = new ArrayList<>();
        String nextUrl = "relationshiptype?v=full";

        try {
            while (nextUrl != null) {
                JsonNode body = openMRSClient.getForEndpoint(nextUrl);
                if (body != null && body.has("results")) {
                    for (JsonNode relationshipType : body.get("results")) {
                        relationshipTypeList.add(new RelationshipTypeDTO(
                                UUID.fromString(relationshipType.path("uuid").asText()),
                                relationshipType.path("description").asText(),
                                relationshipType.path("aIsToB").asText(),
                                relationshipType.path("bIsToA").asText(),
                                relationshipType.path("weight").asInt(),
                                relationshipType.path("display").asText()
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

            log.info("✅ RelationshipTypes erfolgreich von OpenMRS in die Middleware geladen.");
        } catch (Exception e) {
            log.error("❌ Fehler beim Laden der RelationshipTypes von OpenMRS: {}", e.getMessage(), e);
        }

        return relationshipTypeList;
    }
}
