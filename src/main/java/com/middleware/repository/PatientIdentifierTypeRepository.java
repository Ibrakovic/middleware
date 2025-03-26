package com.middleware.repository;

import com.middleware.model.PatientIdentifierTypeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PatientIdentifierTypeRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Save a patient identifier type to the database
     * @param patientIdentifierType the patient identifier type to save
     * @return a message indicating the success or failure of the operation
     */
    public String savePatientIdentifierTypeRepository (PatientIdentifierTypeDTO patientIdentifierType) {
        String sql = """
        INSERT INTO patient_identifier_type (uuid, name, description, format, format_description, required, validator)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (uuid) DO UPDATE
        SET name = EXCLUDED.name,
            description = EXCLUDED.description,
            format = EXCLUDED.format,
            format_description = EXCLUDED.format_description,
            required = EXCLUDED.required,
            validator = EXCLUDED.validator
        """;

        try {
            jdbcTemplate.update(sql,
                    patientIdentifierType.getUuid(),
                    patientIdentifierType.getName(),
                    patientIdentifierType.getDescription(),
                    patientIdentifierType.getFormat(),
                    patientIdentifierType.getFormatDescription(),
                    patientIdentifierType.isRequired(),
                    patientIdentifierType.getValidator());
            return "✅ PatientIdentifierType erfolgreich gespeichert: " + patientIdentifierType.getName();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}, {}",
                    sql, patientIdentifierType.getUuid(), patientIdentifierType.getName(), patientIdentifierType.getDescription(),
                    patientIdentifierType.getFormat(), patientIdentifierType.getFormatDescription(), patientIdentifierType.isRequired(),
                    patientIdentifierType.getValidator());
            return "❌ Fehler beim Speichern des PatientIdentifierType " + patientIdentifierType.getName() + ": " + e.getMessage();
        }
    }
}
