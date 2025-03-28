package com.middleware.service;

import com.middleware.model.PatientDTO;
import com.middleware.model.VisitDTO;
import com.middleware.model.VisitTypeDTO;
import com.middleware.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = {
        VisitService.class,
        VisitRepository.class,
        VisitTypeService.class,
        PatientService.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class VisitServiceTest {

    @Autowired
    private VisitService visitService;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private VisitTypeService visitTypeService;

    @Autowired
    private PatientService patientService;

    private UUID patientUUID;
    private UUID visitTypeUUID;

    @BeforeEach
    public void setUp() {

        patientUUID = UUID.randomUUID();
        visitTypeUUID = UUID.randomUUID();

        PatientDTO patientDTO = new PatientDTO(
                patientUUID,
                "Test Patient",
                "Max Mustermann",
                "M",
                25,
                "1996-01-01T00:00:00.000+0000"
        );
        patientService.savePatientToDatabase(List.of(patientDTO));

        VisitTypeDTO visitTypeDTO = new VisitTypeDTO(
                visitTypeUUID,
                "Test Visit Type",
                "Test Visit Type Description",
                false
        );
        visitTypeService.saveVisitTypeToDatabase(List.of(visitTypeDTO));
    }

    @Test
    public void testSaveVisit() {
        VisitDTO visitDTO = new VisitDTO(
                UUID.randomUUID(),
                "Test Visit",
                patientUUID,
                "Test Patient",
                visitTypeUUID,
                "Test Visit Type",
                UUID.randomUUID(),
                "Test Location",
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC).plusHours(1),
                UUID.randomUUID(),
                "Test Encounter"
        );

        visitService.saveVisitToDatabase(List.of(visitDTO));
        VisitDTO savedVisit = visitRepository.findById(visitDTO.getUuid());
        assertThat(savedVisit).isNotNull();

        assertThat(savedVisit.getDisplay()).isEqualTo("Test Visit");
        assertThat(savedVisit.getUuid()).isEqualTo(visitDTO.getUuid());
    }

    @Test
    public void testWithWrongDisplay() {
        VisitDTO visitDTO = new VisitDTO(
                UUID.randomUUID(),
                "Test Visit",
                patientUUID,
                "Test Patient",
                visitTypeUUID,
                "Test Visit Type",
                UUID.randomUUID(),
                "Test Location",
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC).plusHours(1),
                UUID.randomUUID(),
                "Test Encounter"
        );

        visitService.saveVisitToDatabase(List.of(visitDTO));
        VisitDTO savedVisit = visitRepository.findById(visitDTO.getUuid());
        assertThat(savedVisit).isNotNull();

        assertThatThrownBy(() -> assertThat(savedVisit.getDisplay()).isEqualTo("Next Visit"))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void testSaveVisitWithNullUUID() {
        VisitDTO visitDTO = new VisitDTO(
                null,
                "Test Visit",
                patientUUID,
                "Test Patient",
                visitTypeUUID,
                "Test Visit Type",
                UUID.randomUUID(),
                "Test Location",
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC).plusHours(1),
                UUID.randomUUID(),
                "Test Encounter"
        );

        assertThatThrownBy(() -> visitService.saveVisitToDatabase(List.of(visitDTO)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UUID darf nicht null sein");
    }
}