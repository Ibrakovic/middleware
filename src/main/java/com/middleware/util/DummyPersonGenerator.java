package com.middleware.util;

import com.middleware.model.PersonDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DummyPersonGenerator {

    /**
     * Generate a list of dummy persons, needed for load testing purposes
     * @param count Number of persons to generate
     * @return List of PersonDTO objects
     */

    public static List<PersonDTO> generate(int count) {
        List<PersonDTO> persons = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            persons.add(new PersonDTO(
                    UUID.randomUUID(),
                    "Person " + i,
                    i % 2 == 0 ? "M" : "F",
                    20 + (i % 50),
                    "1990-01-01T00:00:00.000+0000"
            ));
        }
        return persons;
    }

}
