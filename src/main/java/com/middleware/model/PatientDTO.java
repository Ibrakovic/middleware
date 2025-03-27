package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/*
* Mandatory fields for recreating a Patient object
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {

    private UUID uuid;
    private String display;
    private String personName;
    private String gender;
    private int age;
    private String birthdate;


}
