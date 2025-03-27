package com.middleware.service;

import com.middleware.model.PersonDTO;
import com.middleware.repository.PersonRepository;
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
        PersonService.class,
        PersonRepository.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testSavePerson() {
        PersonDTO personDTO = new PersonDTO(
                UUID.randomUUID(),
                "Test Person",
                "M",
                25,
                "1996-01-01"
        );

        personService.savePersonToDatabase(List.of(personDTO));
        PersonDTO personToSave = personRepository.findById(personDTO.getUuid());
        assertThat(personToSave).isNotNull();

        PersonDTO savedPerson = personRepository.findById(personDTO.getUuid());
        assertThat(savedPerson).isNotNull();
        assertThat(savedPerson.getDisplay()).isEqualTo("Test Person");
    }

    @Test
    public void testWithWrongDisplay() {
        PersonDTO personDTO = new PersonDTO(
                UUID.randomUUID(),
                "Test Person",
                "M",
                25,
                "1996-01-01"
        );

        personService.savePersonToDatabase(List.of(personDTO));
        PersonDTO personToSave = personRepository.findById(personDTO.getUuid());
        assertThatThrownBy(() -> assertThat(personToSave.getDisplay()).isEqualTo("Next Patient"))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void testWithNullUUID() {
        PersonDTO personDTO = new PersonDTO(
                null,
                "Test Person",
                "M",
                25,
                "1996-01-01"
        );

        assertThatThrownBy(() -> personService.savePersonToDatabase(List.of(personDTO)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UUID darf nicht null sein");
    }
}
