package com.middleware.repository;

import com.middleware.model.VisitTypeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VisitTypeRepository {

    private final JdbcTemplate jdbcTemplate;

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
            return "✅ Besuchstyp erfolgreich gespeichert: " + visitType.getName();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}",
                    sql, visitType.getUuid(), visitType.getName(), visitType.getDescription(), visitType.isRetired());
            return "❌ Fehler beim Speichern des Besuchstyps " + visitType.getName() + ": " + e.getMessage();
        }
    }
}
