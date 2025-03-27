package com.middleware.service;

import com.middleware.model.ConceptDTO;
import com.middleware.model.DrugDTO;
import com.middleware.repository.DrugRepository;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = {
        DrugService.class,
        DrugRepository.class,
        ConceptService.class
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class DrugServiceTest {
    @Autowired
    private DrugService drugService;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private ConceptService conceptService;

    @Test
    public void testSaveDrug() {

        // Create a concept to be used in the drug, because a drug needs a concept to be saved
        UUID conceptUuid = UUID.randomUUID();
        ConceptDTO conceptDTO = new ConceptDTO(
                conceptUuid,
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

        DrugDTO drug = new DrugDTO(
                UUID.randomUUID(),
                "Test Drug",
                "500mg",
                "1000mg",
                "250mg",
                false,
                conceptUuid,
                false,
                "Tablet"
        );

        drugService.saveDrugsToDatabase(List.of(drug));
        DrugDTO savedDrug = drugRepository.findById(drug.getUuid());
        assertThat(savedDrug).isNotNull();

        assertThat(savedDrug.getName()).isEqualTo("Test Drug");

    }

    @Test
    public void testWithWrongName() {
        DrugDTO drug = new DrugDTO(
                UUID.randomUUID(),
                "Falscher Name",
                "500mg",
                "1000mg",
                "250mg",
                false,
                UUID.randomUUID(),
                false,
                "Tablet"
        );

        drugService.saveDrugsToDatabase(List.of(drug));

        assertThatThrownBy(() -> assertThat(drug.getName()).isEqualTo("Richtiger Name"))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void testSaveDrugWithNullUUID() {
        DrugDTO drug = new DrugDTO(
                null,
                "Test Drug",
                "500mg",
                "1000mg",
                "250mg",
                false,
                UUID.randomUUID(),
                false,
                "Tablet"
        );

        assertThatThrownBy(() -> drugService.saveDrugsToDatabase(List.of(drug)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UUID darf nicht null sein");
    }

}
