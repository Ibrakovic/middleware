package com.middleware.service;

import com.middleware.model.VisitTypeDTO;
import com.middleware.repository.VisitRepository;
import com.middleware.repository.VisitTypeRepository;
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
        VisitTypeService.class,
        VisitTypeRepository.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class VisitTypeServiceTest {

        @Autowired
        private VisitTypeService visitTypeService;

        @Autowired
        private VisitTypeRepository visitTypeRepository;

        @Test
        public void testSaveVisitType() {
            VisitTypeDTO visitTypeDTO = new VisitTypeDTO(
                    UUID.randomUUID(),
                    "Test Visit Type",
                    "This is a test visit type",
                    false
            );
            visitTypeService.saveVisitTypeToDatabase(List.of(visitTypeDTO));
            VisitTypeDTO savedVisitType = visitTypeRepository.findById(visitTypeDTO.getUuid());

            assertThat(savedVisitType).isNotNull();
            assertThat(savedVisitType.getUuid()).isNotNull();
            assertThat(savedVisitType.getUuid()).isEqualTo(visitTypeDTO.getUuid());
            assertThat(savedVisitType.getName()).isEqualTo("Test Visit Type");
            assertThat(savedVisitType.isRetired()).isFalse();
        }

        @Test
        public void testSaveVisitTypeWithWrongName() {
            VisitTypeDTO visitTypeDTO = new VisitTypeDTO(
                    UUID.randomUUID(),
                    "Test",
                    "This is a test visit type",
                    false
            );

            visitTypeService.saveVisitTypeToDatabase(List.of(visitTypeDTO));
            VisitTypeDTO savedVisitType = visitTypeRepository.findById(visitTypeDTO.getUuid());
            assertThat(savedVisitType).isNotNull();

            assertThatThrownBy(() -> assertThat(savedVisitType.getName()).isEqualTo("Test Visit Type"))
                    .isInstanceOf(AssertionError.class);
        }

        @Test
        public void testSaveVisitTypeWithNullUUID() {
            VisitTypeDTO visitTypeDTO = new VisitTypeDTO(
                    null,
                    "Test Visit Type",
                    "This is a test visit type",
                    false
            );

            assertThatThrownBy(() -> visitTypeService.saveVisitTypeToDatabase(List.of(visitTypeDTO)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("UUID darf nicht null sein");
        }
}
