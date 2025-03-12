package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.PatientDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final OpenMRSClient openMRSClient;

    public PatientService(OpenMRSClient openMRSClient) {
        this.openMRSClient = openMRSClient;
    }

    public List<PatientDTO> getAllPatients() {
        List<PatientDTO> patientDTOs = new ArrayList<>();
        JsonNode patientsJson = openMRSClient.getAllPatients();

        if (patientsJson != null && patientsJson.isArray()) {
            for (JsonNode patientJson : patientsJson) {
                PatientDTO dto = mapJsonToPatientDTO(patientJson);
                patientDTOs.add(dto);
            }
        }

        return patientDTOs;
    }

    private PatientDTO mapJsonToPatientDTO(JsonNode patientJson) {
        UUID uuid = UUID.fromString(patientJson.path("uuid").asText());
        String display = patientJson.path("display").asText();

        JsonNode personNode = patientJson.path("person");
        String personName = personNode.path("display").asText();
        String gender = personNode.path("gender").asText();
        int age = personNode.path("age").asInt();
        String birthdate = personNode.path("birthdate").asText();

        return new PatientDTO(uuid, display, personName, gender, age, birthdate);
    }
}
