package com.middleware.repository;

import com.middleware.model.ConceptDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ConceptRepository {

    private final JdbcTemplate jdbcTemplate;

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
            return "✅ Concept erfolgreich gespeichert: " + conceptDTO.getName();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}, {}, {}, {}",
                    sql, conceptDTO.getUuid(), conceptDTO.getName(), conceptDTO.getConceptClassName(),
                    conceptDTO.getConceptClassUuid(), conceptDTO.getConceptClassDescription(),
                    conceptDTO.getDescriptionsUuid(), conceptDTO.getDescriptionsDescription(),
                    conceptDTO.getDatatypeUuid(), conceptDTO.getVersion());
            return "❌ Fehler beim Speichern des Concepts " + conceptDTO.getName() + ": " + e.getMessage();
        }
    }
}
