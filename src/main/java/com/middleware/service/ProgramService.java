package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.ProgramDTO;
import com.middleware.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final OpenMRSClient openMRSClient;
    private final ProgramRepository programRepository;

    /**
     * Get all programs from OpenMRS
     * @return List of ProgramDTO
     */
    public List<ProgramDTO> getAllPrograms() {
        List<ProgramDTO> programList = new ArrayList<>();
        String nextUrl = "program?v=full";

        try {
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
                                UUID.fromString(concept.path("uuid").asText()),
                                conceptName.path("name").asText(),
                                conceptDescription != null ? conceptDescription.path("display").asText() : null,
                                outcomesConceptName.path("display").asText(),
                                outcomesConcept.path("uuid").asText().isBlank() ? null : UUID.fromString(outcomesConcept.path("uuid").asText()),
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

            log.info("Programs erfolgreich von OpenMRS in die Middleware geladen.");
        } catch (Exception e) {
            log.error("Fehler beim Laden der Programs von OpenMRS: {}", e.getMessage(), e);
        }

        return programList;
    }

    /**
     * Save programs to the database
     * @param programs List of programs to save
     */
    public void saveProgramToDatabase(List<ProgramDTO> programs) {
        log.info("Programs speichern beginnt");

        for (ProgramDTO program : programs) {
            if (program.getUuid() == null) {
                throw new IllegalArgumentException("UUID darf nicht null sein");
            }

            try {
                String result = programRepository.saveProgram(program);
                log.info(result);
            } catch (IllegalArgumentException e) {
                log.error("Fehler beim Speichern des Programs in die Datenbank {}: {}", program.getUuid(), e.getMessage(), e);
                throw new IllegalArgumentException("Fehler beim Speichern des Programs", e);
            }
        }

        log.info("Programs speichern beendet");
    }
}
