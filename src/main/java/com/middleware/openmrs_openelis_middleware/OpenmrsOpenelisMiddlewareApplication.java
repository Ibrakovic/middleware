package com.middleware.openmrs_openelis_middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.middleware.model.PatientDTO;
import com.middleware.service.PatientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@SpringBootApplication(scanBasePackages = "com.middleware")
public class OpenmrsOpenelisMiddlewareApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenmrsOpenelisMiddlewareApplication.class, args);
	}

	@Bean
	CommandLineRunner run(PatientService patientService) {
		return args -> {
			List<PatientDTO> patients = patientService.getAllPatients();

			// Ergebnis in eine .txt-Datei schreiben
			try (FileWriter writer = new FileWriter("patients_proof_of_concept.txt")) {
				ObjectMapper objectMapper = new ObjectMapper();
				writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(patients));
				System.out.println("Patientendaten erfolgreich gespeichert in 'patients_proof_of_concept.txt'");
			} catch (IOException e) {
				System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
			}
		};
	}
}
