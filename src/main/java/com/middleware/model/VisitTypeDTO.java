package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Mandatory fields for recording a visit type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class VisitTypeDTO {
    private UUID uuid;
    private String name;
    private String description;
    private boolean retired;
}
