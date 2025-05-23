package com.middleware.repository;

import com.middleware.model.PatientDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PatientRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     *  Save a patient to the database.
     * @param patient The patient to be saved.
     * @return A message indicating the success or failure of the operation.
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public String savePatient(PatientDTO patient) {
        String sql = """
    INSERT INTO patient (uuid, display, person_name, gender, age, birthdate)
    VALUES (?, ?, ?, ?, ?, ?)
    ON CONFLICT (uuid) DO UPDATE
    SET display = EXCLUDED.display,
        person_name = EXCLUDED.person_name,
        gender = EXCLUDED.gender,
        age = EXCLUDED.age,
        birthdate = EXCLUDED.birthdate
    """;


        try {
            jdbcTemplate.update(sql,
                    patient.getUuid(),
                    patient.getDisplay(),
                    patient.getPersonName(),
                    patient.getGender(),
                    patient.getAge(),
                    formatDate(patient.getBirthdate()));
            return "Patient erfolgreich gespeichert in die Datenbank in der Cloud: " + patient.getUuid();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}",
                    sql, patient.getUuid(), patient.getDisplay(), patient.getPersonName(),
                    patient.getGender(), patient.getAge(), patient.getBirthdate());
            return "Fehler beim Speichern von Patient " + patient.getUuid() + " in die Datenbank in der Cloud: " + e.getMessage();
        }
    }

    /**
     * Converts a string date to a java.sql.Date object.
     */
    private java.sql.Date formatDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;  // If the date is null or empty, return null
        }

        try {
            // Custom pattern to match "1962-03-04T00:00:00.000+0100"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            LocalDate localDate = LocalDateTime.parse(date, formatter).toLocalDate();
            return java.sql.Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            log.error("Fehler beim Formatieren des Datums: {}", date, e);
            return null;  // If the date is not in the expected format
        }
    }

    /**
     * Retrieves a patient by its UUID. (Needed for testing and can be used for future features)
     * @param uuid UUID of the patient to retrieve.
     * @return PatientDTO object representing the patient.
     */
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 300000L) // 5 Minuten
    )
    public PatientDTO findById(UUID uuid) {
        String sql = """
        SELECT UUID, DISPLAY, PERSON_NAME, GENDER, AGE, BIRTHDATE
        FROM PATIENT WHERE UUID = ?
        """;

        try {
            PatientDTO patient = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new PatientDTO(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("display"),
                    rs.getString("person_name"),
                    rs.getString("gender"),
                    rs.getInt("age"),
                    rs.getString("birthdate")
            ), uuid);

            log.info("Patient erfolgreich aus der Datenbank geladen: {}", uuid);
            return patient;

        } catch (Exception e) {
            log.error("Fehler beim Laden des Patienten mit UUID {} aus der Datenbank: {}", uuid, e.getMessage(), e);
            return null;
        }
    }
}
