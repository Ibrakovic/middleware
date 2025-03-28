package com.middleware.service;

import com.middleware.model.RelationshipTypeDTO;
import com.middleware.repository.ProgramRepository;
import com.middleware.repository.RelationshipTypeRepository;
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
        RelationshipTypeService.class,
        RelationshipTypeRepository.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class RelationshipTypeServiceTest {

    @Autowired
    private RelationshipTypeService relationshipTypeService;

    @Autowired
    private RelationshipTypeRepository relationshipTypeRepository;

    @Test
    public void testSaveRelationshipType() {
        RelationshipTypeDTO relationshipTypeDTO = new RelationshipTypeDTO(
                UUID.randomUUID(),
                "description",
                "aIsToB",
                "bIsToA",
                1,
                "display"
        );

        relationshipTypeService.saveRelationshipTypeToDatabase(List.of(relationshipTypeDTO));
        RelationshipTypeDTO relationshipTypeToSave = relationshipTypeRepository.findById(relationshipTypeDTO.getUuid());
        assertThat(relationshipTypeToSave).isEqualTo(relationshipTypeDTO);

        RelationshipTypeDTO savedRelationshipType = relationshipTypeRepository.findById(relationshipTypeDTO.getUuid());
        assertThat(savedRelationshipType).isNotNull();
        assertThat(savedRelationshipType.getDescription()).isEqualTo("description");
    }

    @Test
    public void testWithWrongDescription() {
        RelationshipTypeDTO relationshipTypeDTO = new RelationshipTypeDTO(
                UUID.randomUUID(),
                "description",
                "aIsToB",
                "bIsToA",
                1,
                "display"
        );

        relationshipTypeService.saveRelationshipTypeToDatabase(List.of(relationshipTypeDTO));
        RelationshipTypeDTO relationshipTypeToSave = relationshipTypeRepository.findById(relationshipTypeDTO.getUuid());
        assertThat(relationshipTypeToSave).isEqualTo(relationshipTypeDTO);

        RelationshipTypeDTO savedRelationshipType = relationshipTypeRepository.findById(relationshipTypeDTO.getUuid());
        assertThat(savedRelationshipType).isNotNull();
        assertThatThrownBy(() -> assertThat(savedRelationshipType.getDescription()).isEqualTo("wrong description"));
    }

    @Test
    public void testSaveRelationshipTypeWithNullUUID() {
        RelationshipTypeDTO relationshipTypeDTO = new RelationshipTypeDTO(
                null,
                "abc",
                "aIsToB",
                "bIsToA",
                1,
                "display"
        );

        assertThatThrownBy(() -> relationshipTypeService.saveRelationshipTypeToDatabase(List.of(relationshipTypeDTO)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UUID darf nicht null sein");
    }
}
