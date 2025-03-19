package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConceptDTO {
    private UUID uuid;
    private String name;
    private String conceptClassName;
    private UUID conceptClassUuid;
    private String conceptClassDescription;
    private UUID descriptionsUuid;
    private String descriptionsDescription;
    private UUID datatypeUuid;
    private String version;
}
