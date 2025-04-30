package com.middleware.repository;

import com.middleware.model.PatientIdentifierTypeDTO;
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
public class PatientIdentifierTypeRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Save a patient identifier type to the database
     * @param patientIdentifierType the patient identifier type to save
     * @return a message indicating the success or failure of the operation
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public String savePatientIdentifierType (PatientIdentifierTypeDTO patientIdentifierType) {
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
            return "PatientIdentifierType erfolgreich gespeichert in die Datenbank in der Cloud: " + patientIdentifierType.getUuid();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}, {}",
                    sql, patientIdentifierType.getUuid(), patientIdentifierType.getName(), patientIdentifierType.getDescription(),
                    patientIdentifierType.getFormat(), patientIdentifierType.getFormatDescription(), patientIdentifierType.isRequired(),
                    patientIdentifierType.getValidator());
            return "Fehler beim Speichern des PatientIdentifierType " + patientIdentifierType.getUuid() + " in die Datenbank in der Cloud: " + e.getMessage();
        }
    }

    /**
     * Retrieves a PatientIdentifierType by its UUID. (Needed for testing and can be used for future features)
     * @param uuid UUID of the PatientIdentifierType to retrieve.
     * @return PatientIdentifierTypeDTO object representing the PatientIdentifierTypeDTO.
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public PatientIdentifierTypeDTO findById(UUID uuid) {
        String sql = """
        SELECT UUID, NAME, DESCRIPTION, FORMAT, FORMAT_DESCRIPTION, REQUIRED, VALIDATOR
        FROM PATIENT_IDENTIFIER_TYPE WHERE UUID = ?
        """;

        try {
            PatientIdentifierTypeDTO identifierType = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new PatientIdentifierTypeDTO(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("format"),
                    rs.getString("format_description"),
                    rs.getBoolean("required"),
                    rs.getString("validator")
            ), uuid);

            log.info("PatientIdentifierType erfolgreich aus der Datenbank geladen: {}", uuid);
            return identifierType;

        } catch (Exception e) {
            log.error("Fehler beim Laden des PatientIdentifierType mit UUID {} aus der Datenbank: {}", uuid, e.getMessage(), e);
            return null;
        }
    }

}
