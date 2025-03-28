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

    /**
     * Create a scheduled task that runs every hour to sync the data from the OpenMRS API to the database.
     * The task will run at the beginning of every hour.
     */
    @Scheduled(cron = "0 0 * * * * ")
    public void hourlyFrequent() {
        String now = LocalDateTime.now().format(FORMATTER);
        log.info("⏰ HourlyScheduler gestartet um: {}", now); //logger

        List<PatientDTO> patients = patientService.getAllPatients();
        patientService.savePatientToDatabase(patients);

        List<PersonDTO> persons = personService.getAllPersons();
        personService.savePersonToDatabase(persons);

        List<ProgramDTO> programs = programService.getAllPrograms();
        programService.saveProgramToDatabase(programs);

        List<PatientIdentifierTypeDTO> patientIdentifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        patientIdentifierTypeService.savePatientIdentifierTypesToDatabase(patientIdentifierTypes);

        List<RelationshipTypeDTO> relationshipTypes = relationshipTypeService.getAllRelationshipTypes();
        relationshipTypeService.saveRelationshipTypeToDatabase(relationshipTypes);

        List<UUID> patientUUIDs = PatientService.getPatientUUIDs(patients);
        List<ObsDTO> obs = obsService.getAllObsForAllPatients(patientUUIDs);
        obsService.saveObsToDatabase(obs);

        log.info("✅ HourlyScheduler abgeschlossen um: {}", LocalDateTime.now().format(FORMATTER)); //logger
    }

}
