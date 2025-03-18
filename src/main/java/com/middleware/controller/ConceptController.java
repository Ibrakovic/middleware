package com.middleware.controller;

import com.middleware.model.ConceptDTO;
import com.middleware.service.ConceptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/concepts")
@RequiredArgsConstructor
public class ConceptController {

    private final ConceptService conceptService;

    @GetMapping
    public List<ConceptDTO> getAllConcepts() {
        return conceptService.getAllConcepts();
    }
}
