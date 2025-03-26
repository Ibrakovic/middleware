package com.middleware.controller;

import com.middleware.model.ConceptDTO;
import com.middleware.service.ConceptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/concept")
@RequiredArgsConstructor
public class ConceptController {

    private final ConceptService conceptService;

    /**
     * Get all concepts
     * @return List of Concepts
     */
    @GetMapping
    public List<ConceptDTO> getAllConcepts() {
        return conceptService.getAllConcepts();
    }
}
