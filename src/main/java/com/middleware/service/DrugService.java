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
     * Saves a list of DrugDTOs to the database.
     * @param drugs List of drugs to be saved.
     */
    public void saveDrugsToDatabase(List<DrugDTO> drugs) {
        log.info("Drugs in die Datenbank speichern beginnt");

        for (DrugDTO drug : drugs) {
            if (drug.getUuid() == null) {
                throw new IllegalArgumentException("UUID darf nicht null sein");
            }
            try {
                String result = drugRepository.saveDrug(drug);
                log.info("✅ Drug erfolgreich gespeichert in die Datenbank: {}", drug.getUuid());
            } catch (Exception e) {
                log.error("❌ Fehler beim Speichern des Drug in die Datenbank  {}: {}", drug.getUuid(), e.getMessage(), e);
                throw new IllegalArgumentException("Fehler beim Speichern der Drug", e);
            }
        }

        log.info("Drugs in die Datenbank speichern beendet");
    }

    /**
     * Retrieves all drugs from OpenMRS.
     * @return List of all DrugDTOs.
     */
    public List<DrugDTO> getAllDrugs() {
        log.info("Datenabruf aller Drugs von OpenMRS beginnt");
        String nextUrl = "drug?v=full";
        List<DrugDTO> allDrugs = new ArrayList<>();

        try {
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

            log.info("✅ Drug erfolgreich von OpenMRS in die Middleware geladen.");
        } catch (Exception e) {
            log.error("❌ Fehler beim Laden der Drug von OpenMRS: {}", e.getMessage(), e);
        }

        return allDrugs;
    }
}
