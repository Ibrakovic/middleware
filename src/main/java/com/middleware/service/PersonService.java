package com.middleware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.middleware.api.OpenMRSClient;
import com.middleware.model.PersonDTO;
import com.middleware.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final OpenMRSClient openMRSClient;
    private final PersonRepository personRepository;

    /**
     * Save persons to the database
     * @param persons List of persons to save
     */
    public void savePersonsToDatabase(List<PersonDTO> persons) {
        log.info("Persons speichern beginnt");
        for (PersonDTO person : persons) {
            String result = personRepository.savePerson(person);
            System.out.println(result);
        }
        log.info("Persons speichern beendet");
    }


    /**
     * Get all persons from OpenMRS
     * @return List of PersonDTO objects
     */
    public List<PersonDTO> getAllPersons() {
        List<PersonDTO> personList = new ArrayList<>();
        String nextUrl = "person?q=all&limit=1&v=default&startIndex=0";

        while (nextUrl != null) { // as long as there is a next URL fetch the data
            JsonNode body = openMRSClient.getForEndpoint(nextUrl);

            if (body != null && body.has("results")) {
                for (JsonNode person : body.get("results")) {

                    personList.add(new PersonDTO(
                            UUID.fromString(person.path("uuid").asText()),
                            person.path("display").asText(),
                            person.path("gender").asText(),
                            person.path("age").asInt(),
                            person.path("birthdate").asText()
                    ));
                }
            }

            nextUrl = null;
            if (body != null && body.has("links")) {
                for (JsonNode link : body.get("links")) {
                    if (link.has("rel") && "next".equals(link.get("rel").asText())) {
                        nextUrl = link.get("uri").asText().replace(OpenMRSClient.BASE_URL, "");
                        break;
                    }
                }
            }
        }

        return personList;
    }
}
