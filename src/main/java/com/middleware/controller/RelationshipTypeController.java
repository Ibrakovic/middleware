package com.middleware.controller;

import com.middleware.model.RelationshipTypeDTO;
import com.middleware.service.RelationshipTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/relationshiptype")
@RequiredArgsConstructor
public class RelationshipTypeController {

    private final RelationshipTypeService relationshipTypeService;

    @GetMapping
    public List<RelationshipTypeDTO> getAllRelationshipTypes() {
        return relationshipTypeService.getAllRelationshipTypes();
    }
}
