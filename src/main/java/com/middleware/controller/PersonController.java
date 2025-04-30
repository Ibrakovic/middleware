package com.middleware.controller;

import com.middleware.model.PersonDTO;
import com.middleware.service.PersonService;
import com.middleware.util.DummyPersonGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    /**
     * Endpoint to generate dummy persons for load testing.
     * Get persons from OpenMRS and optionally save them to the database.
     * @param limit number of persons to process (optional)
     * @param save if true, save to database
     * @return confirmation message
     */
    @GetMapping
    public ResponseEntity<String> getPersons(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "false") boolean save
    ) {
        List<PersonDTO> dummyPersons = DummyPersonGenerator.generate(limit);

        if (save) {
            personService.savePersonToDatabase(dummyPersons);
            return ResponseEntity.ok(dummyPersons.size() + " Dummy-Personen generiert und gespeichert.");
        } else {
            return ResponseEntity.ok(dummyPersons.size() + " Dummy-Personen generiert (nicht gespeichert nur generiert).");
        }
    }
}
