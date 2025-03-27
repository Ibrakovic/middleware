package com.middleware.service;

import com.middleware.model.ConceptDTO;
import com.middleware.model.ObsDTO;
import com.middleware.model.PatientDTO;
import com.middleware.repository.ConceptRepository;
import com.middleware.repository.ObsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {
        ObsService.class,
        ObsRepository.class,
        PatientService.class,
        ConceptService.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class ObsServiceTest {

    @Autowired
    private ObsService obsService;

    @Autowired
    private ObsRepository obsRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ConceptService conceptService;

    private UUID patientUUID;
    private UUID conceptUUID;

    @BeforeEach
    public void setup() {
        patientUUID = UUID.randomUUID();
        PatientDTO patientDTO = new PatientDTO(
                patientUUID,
                "Test Patient",
                "Max Mustermann",
                "M",
                43,
                "20.12.1978"
        );
        patientService.savePatientToDatabase(List.of(patientDTO));

        conceptUUID = UUID.randomUUID();
        ConceptDTO conceptDTO = new ConceptDTO(
                conceptUUID,
                "Concept für Drug",
                "Test Class",
                UUID.randomUUID(),
                "Beschreibung",
                UUID.randomUUID(),
                "Beschreibung 2",
                UUID.randomUUID(),
                "1.0"
        );
        conceptService.saveConceptsToDatabase(List.of(conceptDTO));
    }


    @Test
    public void testSaveObs() {

        ObsDTO obsDTO = new ObsDTO(
                UUID.randomUUID(),
                "Test Display",
                patientUUID,
                OffsetDateTime.now(),
                conceptUUID,
                "Test Concept",
                UUID.randomUUID()
        );

        obsService.saveObsToDatabase(List.of(obsDTO));
        ObsDTO savedObs = obsRepository.findById(obsDTO.getUuid());
        assertThat(savedObs).isNotNull();

        assertThat(savedObs.getDisplay()).isEqualTo("Test Display");
    }

    @Test
    public void testWithWrongName() {

        ObsDTO obsDTO = new ObsDTO(
                UUID.randomUUID(),
                "Falscher Display",
                patientUUID,
                OffsetDateTime.now(),
                conceptUUID,
                "Test Concept",
                UUID.randomUUID()
        );

        obsService.saveObsToDatabase(List.of(obsDTO));
        ObsDTO savedObs = obsRepository.findById(obsDTO.getUuid());
        assertThatThrownBy(() -> assertThat(savedObs.getDisplay()).isEqualTo("Richtiger Display"))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void testSaveObsWithNullUUID() {

        ObsDTO obsDTO = new ObsDTO(
                null,
                null,
                patientUUID,
                OffsetDateTime.now(),
                conceptUUID,
                "Test Concept",
                UUID.randomUUID()
        );

        assertThatThrownBy(() -> obsService.saveObsToDatabase(List.of(obsDTO)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UUID darf nicht null sein");
    }
}
