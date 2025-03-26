package com.middleware.controller;

import com.middleware.model.PatientIdentifierTypeDTO;
import com.middleware.service.PatientIdentifierTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/patientidentifiertype")
@RequiredArgsConstructor
public class PatientIdentifierTypeController {

    private final PatientIdentifierTypeService patientIdentifierTypeService;

    /**
     * Get all patient identifier types
     * @return List of patient identifier types
     */
    @GetMapping
    public List<PatientIdentifierTypeDTO> getAllPatientIdentifierTypes() {
        return patientIdentifierTypeService.getAllPatientIdentifierTypes();
    }
}
