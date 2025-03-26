package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/*
 * Mandatory fields for recreating a PatientIdentifierType object.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientIdentifierTypeDTO {
    private UUID uuid;
    private String name;
    private String description;
    private String format;
    private String formatDescription;
    private boolean required;
    private String validator;
}
