package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.ConceptDTO;
import com.middleware.repository.ConceptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConceptService {
    private final OpenMRSClient openMRSClient;
    private final ConceptRepository conceptRepository;

    public void saveConceptsToDatabase(List<ConceptDTO> concepts) {
        for (ConceptDTO concept : concepts) {
            String result = conceptRepository.saveConcept(concept);
            System.out.println(result);
        }
        System.out.println("✅ Erfolgreich " + concepts.size() + " Concepts gespeichert.");
    }

    public List<ConceptDTO> getAllConcepts() {
        List<ConceptDTO> conceptList = new ArrayList<>();
        String nextUrl = "concept?term=38341003&source=SNOMED%20CT&limit=1&v=full";

        while (nextUrl != null) {
            JsonNode body = openMRSClient.getForEndpoint(nextUrl);
            if (body != null && body.has("results")) {
                for (JsonNode concept : body.get("results")) {
                    JsonNode conceptClass = concept.path("conceptClass");
                    JsonNode description = concept.path("descriptions").isArray()
                            ? concept.path("descriptions").get(0)
                            : null;
                    JsonNode datatype = concept.path("datatype");

                    conceptList.add(new ConceptDTO(
                            UUID.fromString(concept.path("uuid").asText()),
                            concept.path("name").path("name").asText(),
                            conceptClass.path("name").asText(),
                            UUID.fromString(conceptClass.path("uuid").asText()),
                            conceptClass.path("description").asText(),
                            description != null ? UUID.fromString(description.path("uuid").asText()) : null,
                            description != null ? description.path("description").asText() : null,
                            datatype.has("uuid") && !datatype.path("uuid").asText().isBlank()
                                    ? UUID.fromString(datatype.path("uuid").asText())
                                    : null,
                            concept.path("version").asText()


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

        return conceptList;
    }
}
