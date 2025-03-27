package com.middleware.repository;

import com.middleware.model.ConceptDTO;
import com.middleware.model.DrugDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DrugRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Save a drug to the database
     * @param drug Drug to be saved
     * @return a message indicating the success or failure of the operation
     */
    public String saveDrug(DrugDTO drug) {
        String sql = """
        INSERT INTO drug (uuid, name, strength, maximum_daily_dose, minimum_daily_dose, retired, concept_uuid, combination, dosage_form)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (uuid) DO UPDATE
        SET name = EXCLUDED.name,
            strength = EXCLUDED.strength,
            maximum_daily_dose = EXCLUDED.maximum_daily_dose,
            minimum_daily_dose = EXCLUDED.minimum_daily_dose,
            retired = EXCLUDED.retired,
            concept_uuid = EXCLUDED.concept_uuid,
            combination = EXCLUDED.combination,
            dosage_form = EXCLUDED.dosage_form
        """;

        try {
            jdbcTemplate.update(sql,
                    drug.getUuid(),
                    drug.getName(),
                    drug.getStrength(),
                    drug.getMaximumDailyDose(),
                    drug.getMinimumDailyDose(),
                    drug.isRetired(),
                    drug.getConceptUuid(),
                    drug.isCombination(),
                    drug.getDosageForm());
            return "✅ Drug erfolgreich gespeichert: " + drug.getName();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}, {}, {}, {}",
                    sql, drug.getUuid(), drug.getName(), drug.getStrength(),
                    drug.getMaximumDailyDose(), drug.getMinimumDailyDose(), drug.isRetired(),
                    drug.getConceptUuid(), drug.isCombination(), drug.getDosageForm());
            return "❌ Fehler beim Speichern des Drugs " + drug.getName() + ": " + e.getMessage();
        }
    }

    /**
     * Retrieves a drug by its UUID. (Needed for testing and can be used for future features)
     * @param uuid UUID of the drug to retrieve.
     * @return DrugDTO object representing the drug.
     */
    public DrugDTO findById(UUID uuid) {
        String sql = """
    SELECT uuid, name, strength, maximum_daily_dose, minimum_daily_dose, retired, concept_uuid, combination, dosage_form 
    FROM drug WHERE uuid = ?
    """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new DrugDTO(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("name"),
                rs.getString("strength"),
                rs.getString("maximum_daily_dose"),
                rs.getString("minimum_daily_dose"),
                rs.getBoolean("retired"),
                UUID.fromString(rs.getString("concept_uuid")),
                rs.getBoolean("combination"),
                rs.getString("dosage_form")
        ), uuid);
    }
}
