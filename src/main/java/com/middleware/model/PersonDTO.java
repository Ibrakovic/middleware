package com.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/*
 * Mandatory fields for recreating a Person object
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private UUID uuid;
    private String display;
    private String gender;
    private int age;
    private String birthdate;
}
