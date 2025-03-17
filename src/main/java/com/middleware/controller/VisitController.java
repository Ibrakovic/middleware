package com.middleware.controller;

import com.middleware.model.VisitDTO;
import com.middleware.service.VisitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/visit")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping("/api/visit")
    public List<VisitDTO> getVisitsLastHour() {
        return visitService.getVisitsFromLastHour();
    }
}
