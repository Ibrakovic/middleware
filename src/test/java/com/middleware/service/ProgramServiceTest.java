package com.middleware.service;

import com.middleware.model.ConceptDTO;
import com.middleware.model.ProgramDTO;
import com.middleware.repository.PersonRepository;
import com.middleware.repository.ProgramRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {
        ProgramService.class,
        ProgramRepository.class,
        ConceptService.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class ProgramServiceTest {

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ConceptService conceptService;

    private UUID conceptUUID;
    private UUID outcomesConceptUUID;

    @BeforeEach
    public void setup() {
        conceptUUID = UUID.randomUUID();
        outcomesConceptUUID = UUID.randomUUID();
        ConceptDTO conceptDTO = new ConceptDTO(
                conceptUUID,
                "Falscher Name",
                "Klasse B",
                UUID.randomUUID(),
                "Beschreibung",
                UUID.randomUUID(),
                "Beschreibung 2",
                UUID.randomUUID(),
                "1.0"
        );

        ConceptDTO outcomesConceptDTO = new ConceptDTO(
                outcomesConceptUUID,
                "Falscher Name",
                "Klasse B",
                UUID.randomUUID(),
                "Beschreibung",
                UUID.randomUUID(),
                "Beschreibung 2",
                UUID.randomUUID(),
                "1.0"
        );

        conceptService.saveConceptsToDatabase(List.of(conceptDTO, outcomesConceptDTO));
    }

    @Test
    public void testSaveProgram() {
        ProgramDTO programDTO = new ProgramDTO(
                UUID.randomUUID(),
                "Test Program",
                conceptUUID,
                "Test Concept",
                "Test Description",
                "Test Outcomes Concept",
                outcomesConceptUUID,
                "Test Outcomes Description"
        );

        programService.saveProgramToDatabase(List.of(programDTO));
        ProgramDTO programToSave = programRepository.findById(programDTO.getUuid());
        assertThat(programToSave).isNotNull();

        ProgramDTO savedProgram = programRepository.findById(programDTO.getUuid());
        assertThat(savedProgram).isNotNull();
        assertThat(savedProgram.getName()).isEqualTo("Test Program");
    }

    @Test
    public void testWithWrongName() {
        ProgramDTO programDTO = new ProgramDTO(
                UUID.randomUUID(),
                "Falscher Name",
                conceptUUID,
                "Test Concept",
                "Test Description",
                "Test Outcomes Concept",
                outcomesConceptUUID,
                "Test Outcomes Description"
        );

        programService.saveProgramToDatabase(List.of(programDTO));

        ProgramDTO saved = programRepository.findById(programDTO.getUuid());
        assertThatThrownBy(() -> assertThat(saved.getName()).isEqualTo("Richtiger Name"))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void testSaveProgramWithNullUUID() {
        ProgramDTO programDTO = new ProgramDTO(
                null,
                "Test Program",
                conceptUUID,
                "Test Concept",
                "Test Description",
                "Test Outcomes Concept",
                outcomesConceptUUID,
                "Test Outcomes Description"
        );

        assertThatThrownBy(() -> programService.saveProgramToDatabase(List.of(programDTO)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UUID darf nicht null sein");
    }

}
