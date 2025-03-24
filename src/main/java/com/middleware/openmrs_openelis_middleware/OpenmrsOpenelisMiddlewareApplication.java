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

//			List<ConceptDTO> concepts = conceptService.getAllConcepts();
//			conceptService.saveConceptsToDatabase(concepts);
//			List<DrugDTO> drugs = drugService.getAllDrugs();
//			drugService.saveDrugsToDatabase(drugs);
//			List<PatientIdentifierTypeDTO> patientIdentifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
//			patientIdentifierTypeService.savePatientIdentifierTypesToDatabase(patientIdentifierTypes);
//			List<PatientDTO> patients = patientService.getAllPatients();
//			patientService.savePatientToDatabase(patients);
			List<PersonDTO> persons = personService.getAllPersons();
			personService.savePersonsToDatabase(persons);
			List<ProgramDTO> programs = programService.getAllPrograms();
			programService.saveProgramsToDatabase(programs);

		};
	}
}
