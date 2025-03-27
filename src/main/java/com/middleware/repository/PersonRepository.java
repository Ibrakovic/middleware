package com.middleware.repository;

import com.middleware.model.ConceptDTO;
import com.middleware.model.PersonDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PersonRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Saves a person to the database.
     * @param person the person to save
     * @return a message indicating the success or failure of the operation
     */
    public String savePerson(PersonDTO person) {
        String sql = """
        INSERT INTO person (uuid, display, gender, age, birthdate)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (uuid) DO UPDATE
        SET display = EXCLUDED.display,
            gender  = EXCLUDED.gender,
            age = EXCLUDED.age,
            birthdate = EXCLUDED.birthdate
        """;

        try {
            jdbcTemplate.update(sql,
                    person.getUuid(),
                    person.getDisplay(),
                    person.getGender(),
                    person.getAge(),
                    formatDate(person.getBirthdate()));
            return "✅ Person erfolgreich gespeichert: " + person.getDisplay();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}",
                    sql, person.getUuid(), person.getDisplay(), person.getGender(),
                    person.getAge(), person.getBirthdate());
            return "❌ Fehler beim Speichern der Person " + person.getDisplay() + ": " + e.getMessage();
        }

    }

    private Date formatDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            LocalDate localDate = LocalDateTime.parse(date, formatter).toLocalDate();
            return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            log.error("❌ Fehler beim Formatieren des Datums: {}", date, e);
            return null;
        }
    }

    /**
     * Retrieves a person by its UUID. (Needed for testing and can be used for future features)
     * @param uuid UUID of the person to retrieve.
     * @return PersonDTO object representing the person.
     */
    public PersonDTO findById(UUID uuid) {
        String sql = """
                SELECT UUID, DISPLAY, GENDER, AGE, BIRTHDATE
    FROM person WHERE UUID = ?
    """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new PersonDTO(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("display"),
                rs.getString("display"),
                rs.getInt("age"),
                rs.getString("birthdate")
        ), uuid);
    }
}
