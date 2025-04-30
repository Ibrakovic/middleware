package com.middleware.repository;

import com.middleware.model.ConceptDTO;
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
public class ConceptRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Saves a concept to the database.
     * @param conceptDTO the concept to save
     * @return a message indicating the success or failure of the operation
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public String saveConcept(ConceptDTO conceptDTO) {
        String sql = """
        INSERT INTO concept (uuid, name, concept_class_name, concept_class_uuid, concept_class_description, descriptions_uuid, descriptions_description, datatype_uuid, version)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (uuid) DO UPDATE
        SET name = EXCLUDED.name,
            concept_class_name = EXCLUDED.concept_class_name,
            concept_class_uuid = EXCLUDED.concept_class_uuid,
            concept_class_description = EXCLUDED.concept_class_description,
            descriptions_uuid = EXCLUDED.descriptions_uuid,
            descriptions_description = EXCLUDED.descriptions_description,
            datatype_uuid = EXCLUDED.datatype_uuid,
            version = EXCLUDED.version
        """;

        try {
            jdbcTemplate.update(sql,
                    conceptDTO.getUuid(),
                    conceptDTO.getName(),
                    conceptDTO.getConceptClassName(),
                    conceptDTO.getConceptClassUuid(),
                    conceptDTO.getConceptClassDescription(),
                    conceptDTO.getDescriptionsUuid(),
                    conceptDTO.getDescriptionsDescription(),
                    conceptDTO.getDatatypeUuid(),
                    conceptDTO.getVersion());
            return "Concept erfolgreich in die Datenbank in der Cloud gespeichert: " + conceptDTO.getName();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}, {}, {}, {}",
                    sql, conceptDTO.getUuid(), conceptDTO.getName(), conceptDTO.getConceptClassName(),
                    conceptDTO.getConceptClassUuid(), conceptDTO.getConceptClassDescription(),
                    conceptDTO.getDescriptionsUuid(), conceptDTO.getDescriptionsDescription(),
                    conceptDTO.getDatatypeUuid(), conceptDTO.getVersion());
            return "Fehler beim Speichern des Concept " + conceptDTO.getUuid() + " in die Datenbank in der Cloud: " + e.getMessage();
        }
    }


    /**
     * Retrieves a concept by its UUID. (Needed for testing and can be used for future features)
     * @param uuid UUID of the concept to retrieve.
     * @return ConceptDTO object representing the concept.
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public ConceptDTO findById(UUID uuid) {
        String sql = """
        SELECT uuid, name, concept_class_name, concept_class_uuid, concept_class_description, descriptions_uuid, descriptions_description, datatype_uuid, version
        FROM concept WHERE uuid = ?
        """;

        try {
            ConceptDTO concept = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ConceptDTO(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"),
                    rs.getString("concept_class_name"),
                    UUID.fromString(rs.getString("concept_class_uuid")),
                    rs.getString("concept_class_description"),
                    rs.getObject("descriptions_uuid") != null ? UUID.fromString(rs.getString("descriptions_uuid")) : null,
                    rs.getString("descriptions_description"),
                    rs.getObject("datatype_uuid") != null ? UUID.fromString(rs.getString("datatype_uuid")) : null,
                    rs.getString("version")
            ), uuid);

            log.info("Concept erfolgreich aus der Datenbank geladen: {}", uuid);
            return concept;

        } catch (Exception e) {
            log.error("Fehler beim Laden des Concepts mit UUID {} aus der Datenbank: {}", uuid, e.getMessage(), e);
            return null;
        }
    }

}
