package com.middleware.repository;

import com.middleware.model.RelationshipTypeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RelationshipTypeRepository  {

    private final JdbcTemplate jdbcTemplate;

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
            return "✅ Relationship type successfully saved: " + rel.getDisplay();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}",
                    sql, rel.getUuid(), rel.getAIsToB(), rel.getBIsToA(),
                    rel.getWeight(), rel.getDescription(), rel.getDisplay());
            return "❌ Error saving relationship type " + rel.getDisplay() + ": " + e.getMessage();
        }
    }
}
