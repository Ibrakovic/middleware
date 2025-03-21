package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.PatientIdentifierTypeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientIdentifierTypeService {
    private final OpenMRSClient openMRSClient;

    public List<PatientIdentifierTypeDTO> getAllPatientIdentifierTypes() {
        List<PatientIdentifierTypeDTO> identifierTypeList = new ArrayList<>();
        String nextUrl = "patientidentifiertype?limit=1&v=default&startIndex=0";

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

        return identifierTypeList;
    }

}
