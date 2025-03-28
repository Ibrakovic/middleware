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

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyScheduler {

    private final DrugService drugService;
    private final VisitService visitService;
    private final VisitTypeService visitTypeService;
    private final ConceptService conceptService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Create a scheduled task that runs every day to sync the data from the OpenMRS API to the database.
     * The task will run at 00:00:00 every day.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void syncDaily() {
        String now = LocalDateTime.now().format(FORMATTER);
        log.info("⏰ DailyScheduler gestartet um: {}", now); //logger

        List<DrugDTO> drugs = drugService.getAllDrugs();
        drugService.saveDrugsToDatabase(drugs);

        List<ConceptDTO> concepts = conceptService.getAllConcepts();
        conceptService.saveConceptsToDatabase(concepts);

        List<VisitTypeDTO> visitTypes = visitTypeService.getAllVisitTypes();
        visitTypeService.saveVisitTypeToDatabase(visitTypes);

        List<VisitDTO> visits = visitService.getVisitsFromLastHour();
        visitService.saveVisitToDatabase(visits);

        log.info("✅ DailyScheduler abgeschlossen um: {}", LocalDateTime.now().format(FORMATTER)); //logger
    }

}
