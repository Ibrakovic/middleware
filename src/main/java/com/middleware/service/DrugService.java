package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.DrugDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DrugService {

    private final OpenMRSClient openMRSClient;

    public DrugService(OpenMRSClient openMRSClient) {
        this.openMRSClient = openMRSClient;
    }

    public List<DrugDTO> getAllDrugs() {
        String endpoint = "drug?v=full";
        JsonNode body = openMRSClient.getForEndpoint(endpoint);
        List<DrugDTO> allDrugs = new ArrayList<>();

        if (body != null && body.has("results")) {
            for (JsonNode drug : body.get("results")) {
                allDrugs.add(new DrugDTO(
                        UUID.fromString(drug.path("uuid").asText()),
                        drug.path("name").asText(),
                        drug.path("strength").asText(null),
                        drug.path("maximumDailyDose").asText(null),
                        drug.path("minimumDailyDose").asText(null),
                        drug.path("retired").asBoolean(false)
                ));
            }
        }

        return allDrugs;
    }
}
