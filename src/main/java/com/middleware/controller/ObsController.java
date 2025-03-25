package com.middleware.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.model.ObsDTO;
import com.middleware.model.PatientDTO;
import com.middleware.service.ObsService;
import com.middleware.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/obs")
@RequiredArgsConstructor
public class ObsController {

    private final ObsService obsService;
    private final PatientService patientService;

    @GetMapping("/{patientUUID}")
    public List<ObsDTO> getObsByPatientUUID(@PathVariable UUID patientUUID) {
        return obsService.getObsByPatientUUID(patientUUID);
    }

    @GetMapping("/allPatients")
    public List<ObsDTO> getAllObs() {
        List<PatientDTO> allPatients = patientService.getAllPatients();
        List<UUID> patientUUIDs = PatientService.getPatientUUIDs(allPatients);
        return obsService.getAllObsForAllPatients(patientUUIDs);
    }
}
