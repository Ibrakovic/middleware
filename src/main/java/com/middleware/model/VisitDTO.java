package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 *  Aus den Demo Daten von OpenMRS wurden die folgenden Attribute für Besuche extrahiert.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitDTO {
    private UUID UUID;
    private String display;
    private UUID patientUUID;
    private String patientDisplay;
    private UUID visitTypeUUID;
    private String visitTypeDisplay;
    private UUID visitLocationUUID;
    private String visitLocationDisplay;
    private String startDatetime;
    private String stopDatetime;
    private List<UUID> encountersUUID;
    private String encounterDisplay;

}
