package com.middleware.repository;

import com.middleware.model.VisitDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VisitRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Save a visit to the database
     * @param visit The visit to save
     * @return A message indicating the success or failure of the operation
     */
    public String saveVisit(VisitDTO visit) {
        String sql = """
        INSERT INTO visit (uuid, display, patient_uuid, patient_display, visit_type_uuid, visit_type_display, visit_location_uuid, visit_location_display, start_datetime, stop_datetime, encounter_uuid, encounter_display)
        VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (uuid) DO UPDATE
        SET display = EXCLUDED.display,
            patient_uuid = EXCLUDED.patient_uuid,
            patient_display = EXCLUDED.patient_display,
            visit_type_uuid = EXCLUDED.visit_type_uuid,
            visit_type_display = EXCLUDED.visit_type_display,
            visit_location_uuid = EXCLUDED.visit_location_uuid,
            visit_location_display = EXCLUDED.visit_location_display,
            start_datetime = EXCLUDED.start_datetime,
            stop_datetime = EXCLUDED.stop_datetime,
            encounter_uuid = EXCLUDED.encounter_uuid,
            encounter_display = EXCLUDED.encounter_display
        """;

        try {
            jdbcTemplate.update(sql,
                    visit.getUuid(),
                    visit.getDisplay(),
                    visit.getPatientUUID(),
                    visit.getPatientDisplay(),
                    visit.getVisitTypeUUID(),
                    visit.getVisitTypeDisplay(),
                    visit.getVisitLocationUUID(),
                    visit.getVisitLocationDisplay(),
                    visit.getStartDatetime(),
                    visit.getStopDatetime(),
                    visit.getEncounterUUID() != null ? visit.getEncounterUUID() : null,
                    visit.getEncounterDisplay() != null ? visit.getEncounterDisplay() : null);
            return "✅ Besuch erfolgreich gespeichert: " + visit.getDisplay();
        } catch (Exception e) {
            log.info("Executing SQL: {} with parameters: {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
                    sql, visit.getUuid(), visit.getDisplay(), visit.getPatientUUID(), visit.getPatientDisplay(),
                    visit.getVisitTypeUUID(), visit.getVisitTypeDisplay(), visit.getVisitLocationUUID(),
                    visit.getVisitLocationDisplay(), visit.getStartDatetime(), visit.getStopDatetime(),
                    visit.getEncounterUUID(), visit.getEncounterDisplay());
            return "❌ Fehler beim Speichern des Besuchs " + visit.getDisplay() + ": " + e.getMessage();
        }
    }

    /**
     * Retrieves a Visit by its UUID. (Needed for testing and can be used for future features)
     * @param uuid UUID of the Visit to retrieve.
     * @return VisitDTO object representing the Visit.
     */
    public VisitDTO findById(UUID uuid) {
        String sql = """
        SELECT uuid, display, patient_uuid, patient_display, visit_type_uuid, visit_type_display,
               visit_location_uuid, visit_location_display, start_datetime, stop_datetime,
               encounter_uuid, encounter_display
        FROM visit
        WHERE uuid = ?
    """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new VisitDTO(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("display"),
                UUID.fromString(rs.getString("patient_uuid")),
                rs.getString("patient_display"),
                UUID.fromString(rs.getString("visit_type_uuid")),
                rs.getString("visit_type_display"),
                UUID.fromString(rs.getString("visit_location_uuid")),
                rs.getString("visit_location_display"),
                rs.getObject("start_datetime", OffsetDateTime.class),
                rs.getObject("stop_datetime", OffsetDateTime.class),
                rs.getString("encounter_uuid") != null ? UUID.fromString(rs.getString("encounter_uuid")) : null,
                rs.getString("encounter_display")
        ), uuid);
    }

}
