package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.PatientIdentifierTypeDTO;
import com.middleware.repository.PatientIdentifierTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientIdentifierTypeService {
    private final OpenMRSClient openMRSClient;
    private final PatientIdentifierTypeRepository patientIdentifierTypeRepository;

    /**
     * Saves a list of PatientIdentifierTypeDTOs to the database.
     * @param patientIdentifierTypes List of identifier types.
     */
    public void savePatientIdentifierTypesToDatabase(List<PatientIdentifierTypeDTO> patientIdentifierTypes) {
        log.info("PatientIdentifier in die Datenbank speichern beginnt");

        for (PatientIdentifierTypeDTO patientIdentifierType : patientIdentifierTypes) {
            if (patientIdentifierType.getUuid() == null) {
                throw new IllegalArgumentException("UUID darf nicht null sein");
            }

            try {
                String result = patientIdentifierTypeRepository.savePatientIdentifierType(patientIdentifierType);
                log.info(result);
            } catch (Exception e) {
                log.error("Fehler beim Speichern des PatientIdentifier in die Datenbank {}: {}", patientIdentifierType.getUuid(), e.getMessage(), e);
                throw new IllegalArgumentException("Fehler beim Speichern des PatientIdentifier", e);
            }
        }

        log.info("PatientIdentifier in die Datenbank speichern beendet");
    }

    /**
     * Retrieves all patient identifier types from OpenMRS.
     * @return List of PatientIdentifierTypeDTOs.
     */
    public List<PatientIdentifierTypeDTO> getAllPatientIdentifierTypes() {
        List<PatientIdentifierTypeDTO> identifierTypeList = new ArrayList<>();
        String nextUrl = "patientidentifiertype?limit=1&v=default&startIndex=0";

        try {
            while (nextUrl != null) {
                JsonNode body = openMRSClient.getForEndpoint(nextUrl);
                if (body != null && body.has("results")) {
                    for (JsonNode identifierType : body.get("results")) {
                        identifierTypeList.add(new PatientIdentifierTypeDTO(
                                UUID.fromString(identifierType.path("uuid").asText()),
                                identifierType.path("name").asText(),
                                identifierType.path("description").asText(),
                                identifierType.path("format").asText(null),
                                identifierType.path("formatDescription").asText(null),
                                identifierType.path("required").asBoolean(false),
                                identifierType.path("validator").asText(null)
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

            log.info("Patient Identifier Types erfolgreich von OpenMRS in die Middleware geladen.");
        } catch (Exception e) {
            log.error("Fehler beim Laden der Patient Identifier Types von OpenMRS: {}", e.getMessage(), e);
        }

        return identifierTypeList;
    }

}
