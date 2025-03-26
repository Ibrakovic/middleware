package com.middleware.repository;

import com.middleware.model.ProgramDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
            return "✅ Program erfolgreich gespeichert: " + program.getName();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}, {}, {}",
                    sql, program.getUuid(), program.getName(), program.getConceptNameUuid(),
                    program.getConceptName(), program.getConceptDescription(), program.getOutcomesConceptName(),
                    program.getOutcomesConceptUuid(), program.getOutcomesConceptDescription());
            return "❌ Fehler beim Speichern des Programms " + program.getName() + ": " + e.getMessage();
        }
    }
}
