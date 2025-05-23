package com.middleware.repository;

import com.middleware.model.ObsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ObsRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Saves an Obs to the database
     * @param obs Obs to save
     * @return A message indicating the success or failure of the operation
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
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
            return "Obs erfolgreich in die Datenbank in der Cloud gespeichert: " + obs.getDisplay();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}, {}",
                    sql, obs.getUuid(), obs.getDisplay(), obs.getPatientUuid(),
                    obs.getObsDatetime(), obs.getConceptUuid(), obs.getConceptName(),
                    obs.getValueUuid());
            return "Fehler beim Speichern des Obs " + obs.getUuid() + " in die Datenbank in der Cloud: " + e.getMessage();
        }
    }

    /**
     * Retrieves an observation by its UUID.
     * @param uuid UUID of the observation to retrieve.
     * @return ObsDTO object representing the observation.
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public ObsDTO findById(UUID uuid) {
        String sql = """
        SELECT uuid, display, patient_uuid, obs_datetime, concept_uuid, concept_name, value_uuid
        FROM obs WHERE uuid = ?
        """;

        try {
            ObsDTO obs = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ObsDTO(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("display"),
                    UUID.fromString(rs.getString("patient_uuid")),
                    rs.getObject("obs_datetime", java.time.OffsetDateTime.class),
                    UUID.fromString(rs.getString("concept_uuid")),
                    rs.getString("concept_name"),
                    UUID.fromString(rs.getString("value_uuid"))
            ), uuid);

            log.info("Obs erfolgreich aus der Datenbank geladen: {}", uuid);
            return obs;

        } catch (Exception e) {
            log.error("Fehler beim Laden des Obs mit UUID {} aus der Datenbank: {}", uuid, e.getMessage(), e);
            return null;
        }
    }
}
