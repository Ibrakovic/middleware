package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.RelationshipTypeDTO;
import com.middleware.repository.RelationshipTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelationshipTypeService {
    private final OpenMRSClient openMRSClient;
    private final RelationshipTypeRepository relationshipTypeRepository;

    public void saveRelationshipTypesToDatabase(List<RelationshipTypeDTO> relationshipTypes) {
        for (RelationshipTypeDTO relationshipType : relationshipTypes) {
            String result = relationshipTypeRepository.saveRelationshipType(relationshipType);
            System.out.println(result);
        }
        System.out.println("✅ Erfolgreich " + relationshipTypes.size() + " Beziehungstypen gespeichert");
    }

    public List<RelationshipTypeDTO> getAllRelationshipTypes() {
        List<RelationshipTypeDTO> relationshipTypeList = new ArrayList<>();
        String nextUrl = "relationshiptype?v=full";

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

        return relationshipTypeList;
    }
}
