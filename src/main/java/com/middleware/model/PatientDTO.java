package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/*
* Aus den Demo Daten von OpenMRS wurden die folgenden Attribute für Patienten extrahiert.
* Diese Attribute werden in der PatientDTO Klasse gespeichert.
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
//person noch mitnehmen? da aus person wird patient gemacht