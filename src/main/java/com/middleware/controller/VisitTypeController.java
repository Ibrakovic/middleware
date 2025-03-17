package com.middleware.controller;

import com.middleware.model.VisitTypeDTO;
import com.middleware.service.VisitTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/visittype")
@RequiredArgsConstructor
public class VisitTypeController {
    private final VisitTypeService visitTypeService;

    @GetMapping
    public List<VisitTypeDTO> getAllVisitTypes() {
        return visitTypeService.getAllVisitTypes();
    }
}
