package com.middleware.scheduler;

import com.middleware.service.*;
import com.middleware.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class HourlyScheduler {

    private final PatientService patientService;
    private final PersonService personService;
    private final ProgramService programService;
    private final ObsService obsService;
    private final PatientIdentifierTypeService patientIdentifierTypeService;
    private final RelationshipTypeService relationshipTypeService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "0 */10 * * * *") // Alle 10 Minuten
    public void syncFrequent() {
        String now = LocalDateTime.now().format(FORMATTER);
        log.info("⏰ FrequentScheduler gestartet um: {}", now);

        List<PatientDTO> patients = patientService.getAllPatients();
        patientService.savePatientToDatabase(patients);

        List<PersonDTO> persons = personService.getAllPersons();
        personService.savePersonsToDatabase(persons);

        List<ProgramDTO> programs = programService.getAllPrograms();
        programService.saveProgramsToDatabase(programs);

        List<PatientIdentifierTypeDTO> patientIdentifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        patientIdentifierTypeService.savePatientIdentifierTypesToDatabase(patientIdentifierTypes);

        List<RelationshipTypeDTO> relationshipTypes = relationshipTypeService.getAllRelationshipTypes();
        relationshipTypeService.saveRelationshipTypesToDatabase(relationshipTypes);

        List<UUID> patientUUIDs = PatientService.getPatientUUIDs(patients);
        List<ObsDTO> obs = obsService.getAllObsForAllPatients(patientUUIDs);
        obsService.saveObsToDatabase(obs);

        log.info("✅ HourlyScheduler abgeschlossen um: {}", LocalDateTime.now().format(FORMATTER));
    }

}
