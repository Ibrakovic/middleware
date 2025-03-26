package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/*
 * Mandatory fields for recreating a Program
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDTO {
    private UUID uuid;
    private String name;
    private UUID conceptNameUuid;
    private String conceptName;
    private String conceptDescription;
    private String outcomesConceptName;
    private UUID outcomesConceptUuid ;
    private String outcomesConceptDescription;
}
