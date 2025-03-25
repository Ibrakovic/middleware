package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ObsDTO {
    private UUID uuid;
    private String display;
    private UUID patientUuid;
    private OffsetDateTime obsDatetime;
    private UUID conceptUuid;
    private String conceptName;
    private UUID valueUuid;
}
