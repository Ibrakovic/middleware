package com.middleware.openmrs_openelis_middleware;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.middleware.model.*;
import com.middleware.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication(scanBasePackages = "com.middleware")
public class OpenmrsOpenelisMiddlewareApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenmrsOpenelisMiddlewareApplication.class, args);
	}

	@Bean
	CommandLineRunner run(PatientService patientService, VisitService visitService, DrugService drugService, VisitTypeService visitTypeService, ObsService obsService, ConceptService conceptService, ProgramService programService, PersonService personService, PatientIdentifierTypeService patientIdentifierTypeService) {
		return args -> {
			List<PatientIdentifierTypeDTO> patientIdentifierTypeDTOS = patientIdentifierTypeService.getAllPatientIdentifierTypes();
//			List<PatientDTO> patients = patientService.getAllPatients();
//			List<VisitDTO> visits = visitService.getVisitsFromLastHour();
//			List<DrugDTO> drugs = drugService.getAllDrugs();
//			List<VisitTypeDTO> visitTypes = visitTypeService.getAllVisitTypes();
//			JsonNode allPatients = patientService.fetchPatients();
//			List<UUID> patientUUIDs = PatientService.getPatientUUIDs(allPatients);
//			List<ObsDTO> obs = obsService.getAllObsForAllPatients(patientUUIDs);
//			List<ConceptDTO> concept = conceptService.getAllConcepts();
//			List<ProgramDTO> programs = programService.getAllPrograms();
//			List<PersonDTO> persons = personService.getAllPersons();
//
//			// Ergebnis in eine .txt-Datei schreiben
//			try (FileWriter writer = new FileWriter("patients_proof_of_concept.txt")) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(patients));
//				writer.write("beeee");
//				System.out.println("Patientendaten erfolgreich gespeichert in 'patients_proof_of_concept.txt'");
//			} catch (IOException e) {
//				System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
//			}
//			try (FileWriter writer = new FileWriter("visits_proof_of_concept.txt")) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(visits));
//				writer.write("debug");
//				System.out.println("Besuchsdaten erfolgreich gespeichert in 'visits_proof_of_concept.txt'");
//			} catch (IOException e) {
//				System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
//			}
//			try (FileWriter writer = new FileWriter("drugs_proof_of_concept.txt")) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(drugs));
//				writer.write("debug");
//				System.out.println("Medikamentendaten erfolgreich gespeichert in 'drugs_proof_of_concept.txt'");
//			} catch (IOException e) {
//				System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
//			}
//			try (FileWriter writer = new FileWriter("visittype_proof_of_concept.txt")) {
//				ObjectMapper
//						objectMapper = new ObjectMapper();
//				writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(visitTypes));
//				writer.write("debug");
//				System.out.println("Visittypes erfolgreich gespeichert in 'visittype_proof_of_concept.txt'");
//			} catch (IOException e) {
//				System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
//
//			}
////			try (FileWriter writer = new FileWriter("obs_proof_of_concept.txt")) {
////				ObjectMapper objectMapper = new ObjectMapper();
////				writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obs));
////				writer.write("debug");
////				System.out.println("Beobachtungsdaten erfolgreich gespeichert in 'obs_proof_of_concept.txt'");
////			} catch (IOException e) {
////				System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
////			}
////			try (FileWriter writer = new FileWriter("concept_proof_of_concept.txt")) {
////				ObjectMapper objectMapper = new ObjectMapper();
////				writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(concept));
////				writer.write("debug");
////				System.out.println("Concepts erfolgreich gespeichert in 'concept_proof_of_concept.txt'");
////			} catch (IOException e) {
////				System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
////			}
////
//			try (FileWriter writer = new FileWriter("programs_proof_of_concept.txt")) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(programs));
//				writer.write("debug");
//				System.out.println("Programs erfolgreich gespeichert in 'programs_proof_of_concept.txt'");
//			} catch (IOException e) {
//				System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
//			}
//			try (FileWriter writer = new FileWriter("persons_proof_of_concept.txt")) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(persons));
//				writer.write("debug");
//				System.out.println("Persons erfolgreich gespeichert in 'persons_proof_of_concept.txt'");
//			} catch (IOException e) {
//				System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
//			}
			try (FileWriter fileWriter = new FileWriter("patientIdentifierTypes.json")) {
				ObjectMapper objectMapper = new ObjectMapper();
				fileWriter.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(patientIdentifierTypeDTOS));
				System.out.println("PatientIdentifierTypes erfolgreich gespeichert in 'patientIdentifierTypes.json'");
			} catch (IOException e) {
				System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
			}

		};
	}
}
