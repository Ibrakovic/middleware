package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Mandatory fields for recreating a relationship type
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipTypeDTO {
    private UUID uuid;
    private String description;
    private String aIsToB;
    private String bIsToA;
    private int weight;
    private String display;
}
