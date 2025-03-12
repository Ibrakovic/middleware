package com.middleware.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.PatientDTO;
import com.middleware.service.PatientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller Klasse um Patientendaten von OpenMRS zu OpenELIS zu mappen.
 */

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final OpenMRSClient openMRSClient;
    private final PatientService patientService;

    public PatientController(OpenMRSClient openMRSClient, PatientService patientService) {
        this.openMRSClient = openMRSClient;
        this.patientService = patientService;
    }

    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return patientService.getAllPatients();
    }


}
