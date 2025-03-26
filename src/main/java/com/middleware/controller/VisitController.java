package com.middleware.controller;

import com.middleware.model.VisitDTO;
import com.middleware.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/visit")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    /**
     * Get all visits from the last hour
     * @return List of Visits
     */
    @GetMapping("/api/visit")
    public List<VisitDTO> getVisitsLastHour() {
        return visitService.getVisitsFromLastHour();
    }
}
