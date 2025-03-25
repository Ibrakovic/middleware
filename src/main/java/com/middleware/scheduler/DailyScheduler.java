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

    @Scheduled(cron = "0 0 * * * *") // Jede volle Stunde
    public void syncHourly() {
        String now = LocalDateTime.now().format(FORMATTER);
        log.info("⏰ HourlyScheduler gestartet um: {}", now);

        List<DrugDTO> drugs = drugService.getAllDrugs();
        drugService.saveDrugsToDatabase(drugs);

        List<ConceptDTO> concepts = conceptService.getAllConcepts();
        conceptService.saveConceptsToDatabase(concepts);

        List<VisitTypeDTO> visitTypes = visitTypeService.getAllVisitTypes();
        visitTypeService.saveVisitTypesToDatabase(visitTypes);

        List<VisitDTO> visits = visitService.getVisitsFromLastHour();
        visitService.saveVisitsToDatabase(visits);

        log.info("✅ DailyScheduler abgeschlossen um: {}", LocalDateTime.now().format(FORMATTER));
    }

}
