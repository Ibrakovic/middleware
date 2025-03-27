package com.middleware.service;

import com.middleware.model.ConceptDTO;
import com.middleware.repository.ConceptRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest (classes = {
    ConceptService.class,
    ConceptRepository.class
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class ConceptServiceTest {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private ConceptRepository conceptRepository;

    @Test
    public void testSaveConcept() {
        ConceptDTO conceptDTO = new ConceptDTO(
                UUID.randomUUID(),
                "Test Concept",
                "Test Class",
                UUID.randomUUID(),
                "Test Description",
                UUID.randomUUID(),
                "Test Description",
                UUID.randomUUID(),
                "1.0"
        );



        conceptService.saveConceptsToDatabase(List.of(conceptDTO));
        ConceptDTO conceptToSave = conceptRepository.findById(conceptDTO.getUuid());
        assertThat(conceptToSave).isNotNull();

        ConceptDTO savedConcept = conceptRepository.findById(conceptDTO.getUuid());
        assertThat(savedConcept).isNotNull();
        assertThat(savedConcept.getName()).isEqualTo("Test Concept");
    }

    @Test
    public void testWithWrongName() {
        ConceptDTO conceptDTO = new ConceptDTO(
                UUID.randomUUID(),
                "Falscher Name",
                "Klasse B",
                UUID.randomUUID(),
                "Beschreibung",
                UUID.randomUUID(),
                "Beschreibung 2",
                UUID.randomUUID(),
                "1.0"
        );

        conceptService.saveConceptsToDatabase(List.of(conceptDTO));

        ConceptDTO saved = conceptRepository.findById(conceptDTO.getUuid());
        assertThatThrownBy(() -> assertThat(saved.getName()).isEqualTo("Richtiger Name"))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void testSaveConceptWithNullUUID() {
        ConceptDTO conceptDTO = new ConceptDTO(
                null,
                "",
                "Klasse B",
                UUID.randomUUID(),
                "Beschreibung",
                UUID.randomUUID(),
                "Beschreibung 2",
                UUID.randomUUID(),
                "1.0"
        );

        assertThatThrownBy(() -> conceptService.saveConceptsToDatabase(List.of(conceptDTO)))
                .isInstanceOf(Exception.class);
    }
}