package com.middleware.repository;

import com.middleware.model.RelationshipTypeDTO;
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
public class RelationshipTypeRepository  {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Save a relationship type to the database
     * @param rel The RelationshipType to save
     * @return A message indicating the success or failure of the operation
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public String saveRelationshipType(RelationshipTypeDTO rel) {
        String sql = """
        INSERT INTO relationship_type (uuid, a_is_to_b, b_is_to_a, weight, description, display)
        VALUES (?, ?, ?, ?, ?, ?)
        ON CONFLICT (uuid) DO UPDATE
        SET a_is_to_b = EXCLUDED.a_is_to_b,
            b_is_to_a = EXCLUDED.b_is_to_a,
            weight = EXCLUDED.weight,
            description = EXCLUDED.description,
            uuid = EXCLUDED.uuid,
            display = EXCLUDED.display
        """;

        try {
            jdbcTemplate.update(sql,
                    rel.getUuid(),
                    rel.getAIsToB(),
                    rel.getBIsToA(),
                    rel.getWeight(),
                    rel.getDescription(),
                    rel.getDisplay()
                    );
            return "Relationship type successfully saved in die Datenbank in der Cloud: " + rel.getUuid();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}",
                    sql, rel.getUuid(), rel.getAIsToB(), rel.getBIsToA(),
                    rel.getWeight(), rel.getDescription(), rel.getDisplay());
            return "Error saving relationship type " + rel.getUuid() + "in die Datenbank in der Cloud: " + e.getMessage();
        }
    }

    /**
     * Retrieves a RelationshipType by its UUID. (Needed for testing and can be used for future features)
     * @param uuid UUID of the RelationshipType to retrieve.
     * @return RelationshipTypeDTO object representing the RelationshipType.
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public RelationshipTypeDTO findById(UUID uuid) {
        String sql = """
        SELECT UUID, a_is_to_b, b_is_to_a, weight, description, display
        FROM relationship_type
        WHERE uuid = ?
        """;

        try {
            RelationshipTypeDTO relationshipType = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new RelationshipTypeDTO(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("description"),
                    rs.getString("a_is_to_b"),
                    rs.getString("b_is_to_a"),
                    rs.getInt("weight"),
                    rs.getString("display")
            ), uuid);

            log.info("RelationshipType erfolgreich aus der Datenbank geladen: {}", uuid);
            return relationshipType;

        } catch (Exception e) {
            log.error("Fehler beim Laden des RelationshipType mit UUID {} aus der Datenbank: {}", uuid, e.getMessage(), e);
            return null;
        }
    }

}
