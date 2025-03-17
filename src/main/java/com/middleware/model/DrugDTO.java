package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugDTO {

    private UUID uuid;
    private String name;
    private String strength;
    private String maximumDailyDose;
    private String minimumDailyDose;
    private boolean retired;

}
