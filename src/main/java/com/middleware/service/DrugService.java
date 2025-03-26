package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.DrugDTO;
import com.middleware.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrugService {

    private final OpenMRSClient openMRSClient;
    private final DrugRepository drugRepository;

    /**
     * Save drugs to the database
     * @param drugs List of drugs to be saved
     */
    public void saveDrugsToDatabase(List<DrugDTO> drugs) {
        log.info ("Drugs speichern beginnt");
        for (DrugDTO drug : drugs) {
            String result = drugRepository.saveDrug(drug);
            System.out.println(result);
        }
        log.info("Drugs speichern beendet");
    }

    /**
     * Get all drugs from OpenMRS
     * @return List of all drugs
     */
    public List<DrugDTO> getAllDrugs() {
        String nextUrl = "drug?v=full";
        List<DrugDTO> allDrugs = new ArrayList<>();

        while (nextUrl != null) { // as long as there is a next URL to fetch more data from OpenMRS
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
