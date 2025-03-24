package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.DrugDTO;
import com.middleware.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DrugService {

    private final OpenMRSClient openMRSClient;
    private final DrugRepository drugRepository;

    public void saveDrugsToDatabase(List<DrugDTO> drugs) {
        for (DrugDTO drug : drugs) {
            String result = drugRepository.saveDrug(drug);
            System.out.println(result);
        }
        System.out.println("✅ Erfolgreich " + drugs.size() + " Drugs gespeichert.");
    }

    public List<DrugDTO> getAllDrugs() {
        String nextUrl = "drug?v=full";
        List<DrugDTO> allDrugs = new ArrayList<>();

        while (nextUrl != null) {
            JsonNode body = openMRSClient.getForEndpoint(nextUrl);

            if (body != null && body.has("results")) {
                for (JsonNode drug : body.get("results")) {
                    JsonNode conceptNode = drug.path("concept");
                    UUID conceptUuid = conceptNode.has("uuid") && !conceptNode.path("uuid").asText().isBlank()
                            ? UUID.fromString(conceptNode.path("uuid").asText())
                            : null;

                    allDrugs.add(new DrugDTO(
                            UUID.fromString(drug.path("uuid").asText()),
                            drug.path("name").asText(null),
                            drug.path("strength").asText(null),
                            drug.path("maximumDailyDose").asText(null),
                            drug.path("minimumDailyDose").asText(null),
                            drug.path("retired").asBoolean(false),
                            conceptUuid,
                            drug.path("combination").asBoolean(false),
                            drug.path("dosageForm").asText(null)
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

        return allDrugs;
    }
}
