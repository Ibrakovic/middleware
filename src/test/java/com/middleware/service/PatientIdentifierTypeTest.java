package com.middleware.service;

import com.middleware.model.PatientIdentifierTypeDTO;
import com.middleware.repository.PatientIdentifierTypeRepository;
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
        PatientIdentifierTypeService.class,
        PatientIdentifierTypeRepository.class
} )
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class PatientIdentifierTypeTest {

    @Autowired
    private PatientIdentifierTypeService patientIdentifierTypeService;

    @Autowired
    private PatientIdentifierTypeRepository patientIdentifierTypeRepository;

    @Test
    public void testSavePatientIdentifierType(){
        PatientIdentifierTypeDTO patientIdentifierTypeDTO = new PatientIdentifierTypeDTO(
                UUID.randomUUID(),
                "Test Patient Identifier Type",
                "Test Description",
                "Test Format",
                "Test Format Description",
                true,
                "Test Validator"
        );

        patientIdentifierTypeService.savePatientIdentifierTypesToDatabase(List.of(patientIdentifierTypeDTO));
        PatientIdentifierTypeDTO patientIdentifierTypeToSave = patientIdentifierTypeRepository.findById(patientIdentifierTypeDTO.getUuid());
        assertThat(patientIdentifierTypeToSave).isNotNull();

        PatientIdentifierTypeDTO savedPatientIdentifierType = patientIdentifierTypeRepository.findById(patientIdentifierTypeDTO.getUuid());
        assertThat(savedPatientIdentifierType).isNotNull();
        assertThat(savedPatientIdentifierType.getName()).isEqualTo("Test Patient Identifier Type");
    }

    @Test
    public void testWithWrongName(){
        PatientIdentifierTypeDTO patientIdentifierTypeDTO = new PatientIdentifierTypeDTO(
                UUID.randomUUID(),
                "Not Patient Identifier Type",
                "Test Description",
                "Test Format",
                "Test Format Description",
                true,
                "Test Validator"
        );

        patientIdentifierTypeService.savePatientIdentifierTypesToDatabase(List.of(patientIdentifierTypeDTO));
        PatientIdentifierTypeDTO patientIdentifierTypeToSave = patientIdentifierTypeRepository.findById(patientIdentifierTypeDTO.getUuid());
        assertThat(patientIdentifierTypeToSave).isNotNull();
        assertThatThrownBy(() -> assertThat(patientIdentifierTypeToSave.getName()).isEqualTo("Patient Identifier Type"))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void testSavePatientIdentifierTypeWithNullUUID(){
        PatientIdentifierTypeDTO patientIdentifierTypeDTO = new PatientIdentifierTypeDTO(
                null,
                "Test Patient Identifier Type",
                "Test Description",
                "Test Format",
                "Test Format Description",
                true,
                "Test Validator"
        );

        assertThatThrownBy(() -> patientIdentifierTypeService.savePatientIdentifierTypesToDatabase(List.of(patientIdentifierTypeDTO)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
