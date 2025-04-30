package com.middleware.repository;

import com.middleware.model.VisitTypeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VisitTypeRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Saves a visit type to the database
     * @param visitType The visit type to be saved
     * @return A message indicating the success or failure of the operation
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public String saveVisitType(VisitTypeDTO visitType) {
        String sql = """
        INSERT INTO visit_type (uuid, name, description, retired)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (uuid) DO UPDATE
        SET name = EXCLUDED.name,
            description = EXCLUDED.description,
            retired = EXCLUDED.retired
        """;

        try {
            jdbcTemplate.update(sql,
                    visitType.getUuid(),
                    visitType.getName(),
                    visitType.getDescription(),
                    visitType.isRetired());
            return "Besuchstyp erfolgreich gespeichert in die Datenbank in der Cloud: " + visitType.getUuid();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}",
                    sql, visitType.getUuid(), visitType.getName(), visitType.getDescription(), visitType.isRetired());
            return "Fehler beim Speichern des Besuchstyps " + visitType.getUuid() + " in die Datenbank in der Cloud: " + e.getMessage();
        }
    }

    /**
     * Retrieves a visit type by its UUID
     * @param uuid The UUID of the visit type
     * @return The visit type if found, null otherwise
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public VisitTypeDTO findById(UUID uuid) {
        String sql = """
        SELECT uuid, name, description, retired
        FROM visit_type
        WHERE uuid = ?
        """;

        try {
            VisitTypeDTO visitType = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new VisitTypeDTO(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBoolean("retired")
            ), uuid);

            log.info("VisitType erfolgreich aus der Datenbank geladen: {}", uuid);
            return visitType;

        } catch (Exception e) {
            log.debug("SQL ausgeführt: {} mit Parameter: {}", sql, uuid);
            log.error("Fehler beim Laden des VisitType mit UUID {} aus der Datenbank: {}", uuid, e.getMessage(), e);
            return null;
        }
    }
}
