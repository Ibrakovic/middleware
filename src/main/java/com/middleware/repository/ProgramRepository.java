package com.middleware.repository;

import com.middleware.model.ProgramDTO;
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
public class ProgramRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Save a program to the database
     * @param program the program to save
     * @return a message indicating the success or failure of the operation
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public String saveProgram(ProgramDTO program) {
        String sql = """
        INSERT INTO program (uuid, name, concept_name_uuid, concept_name, concept_description, outcomes_concept_name, outcomes_concept_uuid, outcomes_concept_description)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (uuid) DO UPDATE
        SET name = EXCLUDED.name,
            concept_name_uuid = EXCLUDED.concept_name_uuid,
            concept_name = EXCLUDED.concept_name,
            concept_description = EXCLUDED.concept_description,
            outcomes_concept_name = EXCLUDED.outcomes_concept_name,
            outcomes_concept_uuid = EXCLUDED.outcomes_concept_uuid,
            outcomes_concept_description = EXCLUDED.outcomes_concept_description
        """;

        try {
            jdbcTemplate.update(sql,
                    program.getUuid(),
                    program.getName(),
                    program.getConceptNameUuid(),
                    program.getConceptName(),
                    program.getConceptDescription(),
                    program.getOutcomesConceptName(),
                    program.getOutcomesConceptUuid(),
                    program.getOutcomesConceptDescription());
            return "Program erfolgreich in die Datenbank in der Cloud gespeichert: " + program.getName();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}, {}, {}",
                    sql, program.getUuid(), program.getName(), program.getConceptNameUuid(),
                    program.getConceptName(), program.getConceptDescription(), program.getOutcomesConceptName(),
                    program.getOutcomesConceptUuid(), program.getOutcomesConceptDescription());
            return "Fehler beim Speichern des Programms " + program.getUuid() + " in die Datenbank in der Cloud: " + e.getMessage();
        }
    }

    /**
     * Retrieves a program by its UUID. (Needed for testing and can be used for future features)
     * @param uuid UUID of the program to retrieve.
     * @return ProgramDTO object representing the program.
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public ProgramDTO findById(UUID uuid) {
        String sql = """
            SELECT UUID, NAME, CONCEPT_NAME_UUID, CONCEPT_NAME, CONCEPT_DESCRIPTION, OUTCOMES_CONCEPT_NAME, OUTCOMES_CONCEPT_UUID, OUTCOMES_CONCEPT_DESCRIPTION
            FROM PROGRAM
            WHERE UUID = ?
            """;

        try {
            ProgramDTO program = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ProgramDTO(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"),
                    UUID.fromString(rs.getString("concept_name_uuid")),
                    rs.getString("concept_name"),
                    rs.getString("concept_description"),
                    rs.getString("outcomes_concept_name"),
                    UUID.fromString(rs.getString("outcomes_concept_uuid")),
                    rs.getString("outcomes_concept_description")
            ), uuid);

            log.info("Program erfolgreich aus der Datenbank geladen: {}", uuid);
            return program;

        } catch (Exception e) {
            log.error("Fehler beim Laden des Program mit UUID {} aus der Datenbank: {}", uuid, e.getMessage(), e);
            return null;
        }
    }
}
