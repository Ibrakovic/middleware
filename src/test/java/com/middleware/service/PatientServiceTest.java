package com.middleware.service;

import com.middleware.model.PatientDTO;
import com.middleware.repository.PatientRepository;
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
        PatientService.class,
        PatientRepository.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class PatientServiceTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    public void testSavePatient() {
        PatientDTO patientDTO = new PatientDTO(
                UUID.randomUUID(),
                "Test Patient",
                "Max Mustermann",
                "M",
                25,
                "1996-01-01"
        );

        patientService.savePatientToDatabase(List.of(patientDTO));
        PatientDTO patientToSave = patientRepository.findById(patientDTO.getUuid());
        assertThat(patientToSave).isNotNull();

        PatientDTO savedPatient = patientRepository.findById(patientDTO.getUuid());
        assertThat(savedPatient).isNotNull();
        assertThat(savedPatient.getDisplay()).isEqualTo("Test Patient");
    }

    @Test
    public void testWithWrongDisplay() {
        PatientDTO patientDTO = new PatientDTO(
                UUID.randomUUID(),
                "Test Patient",
                "Max Mustermann",
                "M",
                25,
                "1996-01-01"
        );

        patientService.savePatientToDatabase(List.of(patientDTO));
        PatientDTO patientToSave = patientRepository.findById(patientDTO.getUuid());
        assertThatThrownBy(() -> assertThat(patientToSave.getDisplay()).isEqualTo("Next Patient"))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void testSavePatientWithNullUUID() {
        PatientDTO patientDTO = new PatientDTO(
                null,
                "Test Patient",
                "Max Mustermann",
                "M",
                25,
                "1996-01-01"
        );

        assertThatThrownBy(() -> patientService.savePatientToDatabase(List.of(patientDTO)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UUID darf nicht null sein");
    }
}
