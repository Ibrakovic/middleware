package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.ProgramDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final OpenMRSClient openMRSClient;

    public List<ProgramDTO> getAllPrograms() {
        List<ProgramDTO> programList = new ArrayList<>();
        String nextUrl = "program?v=full";

        while (nextUrl != null) {
            JsonNode body = openMRSClient.getForEndpoint(nextUrl);
            if (body != null && body.has("results")) {
                for (JsonNode program : body.get("results")) {
                    JsonNode concept = program.path("concept");
                    JsonNode outcomesConcept = program.path("outcomesConcept");
                    JsonNode conceptName = concept.path("name");
                    JsonNode conceptDescription = concept.path("descriptions").isArray()
                            ? concept.path("descriptions").get(0)
                            : null;
                    JsonNode outcomesConceptName = outcomesConcept.path("name");
                    JsonNode outcomesConceptDescription = outcomesConcept.path("descriptions").isArray()
                            ? outcomesConcept.path("descriptions").get(0)
                            : null;

                    programList.add(new ProgramDTO(
                            UUID.fromString(program.path("uuid").asText()),
                            program.path("name").asText(),
                            UUID.fromString(conceptName.path("uuid").asText()),
                            conceptName.path("name").asText(),
                            conceptDescription != null ? conceptDescription.path("display").asText() : null,
                            outcomesConceptName.path("display").asText(),
                            outcomesConceptName.path("uuid").asText().isBlank() ? null : UUID.fromString(outcomesConceptName.path("uuid").asText()),
                            outcomesConceptDescription != null ? outcomesConceptDescription.path("display").asText() : null
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

        return programList;
    }
}
