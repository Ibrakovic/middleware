package com.middleware.controller;

import com.middleware.model.DrugDTO;
import com.middleware.service.DrugService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/drug")
@RequiredArgsConstructor
public class DrugController {

    private final DrugService drugService;

    /**
     * Get all drugs
     * @return List of Drugs
     */
    @GetMapping
    public List<DrugDTO> getAllDrugs() {

        return drugService.getAllDrugs();
    }
}
