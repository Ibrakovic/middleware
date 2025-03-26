package com.middleware.controller;

import com.middleware.model.PersonDTO;
import com.middleware.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    /**
     * Get all persons
     * @return List of PersonDTO
     */
    @GetMapping
    public List<PersonDTO> getAllPersons() {
        return personService.getAllPersons();
    }
}
