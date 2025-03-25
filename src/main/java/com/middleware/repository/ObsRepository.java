package com.middleware.repository;

import com.middleware.model.ObsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ObsRepository {

    private final JdbcTemplate jdbcTemplate;

    public String saveObs(ObsDTO obs) {
        String sql = """
        INSERT INTO obs (uuid, display, patient_uuid, obs_datetime, concept_uuid, concept_name, value_uuid)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (uuid) DO UPDATE
        SET display = EXCLUDED.display,
            patient_uuid = EXCLUDED.patient_uuid,
            obs_datetime = EXCLUDED.obs_datetime,
            concept_uuid = EXCLUDED.concept_uuid,
            concept_name = EXCLUDED.concept_name,
            value_uuid = EXCLUDED.value_uuid
        """;

        try {
            jdbcTemplate.update(sql,
                    obs.getUuid(),
                    obs.getDisplay(),
                    obs.getPatientUuid(),
                    obs.getObsDatetime(),
                    obs.getConceptUuid(),
                    obs.getConceptName(),
                    obs.getValueUuid());
            return "✅ Obs erfolgreich gespeichert: " + obs.getDisplay();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}, {}",
                    sql, obs.getUuid(), obs.getDisplay(), obs.getPatientUuid(),
                    obs.getObsDatetime(), obs.getConceptUuid(), obs.getConceptName(),
                    obs.getValueUuid());
            return "❌ Fehler beim Speichern des Obs " + obs.getDisplay() + ": " + e.getMessage();
        }
    }
}
