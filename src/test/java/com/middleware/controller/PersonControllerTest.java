package com.middleware.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.middleware.controller.PersonController.class)
@AutoConfigureMockMvc
@ComponentScan(basePackages = "com.middleware")
@Transactional
@Rollback
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testOnlyLoadPersonsWithoutSaving() throws Exception {
        mockMvc.perform(get("/api/person")
                        .param("limit", "50")
                        .header("Authorization", basicAuth("admin", "testtest")))
                .andExpect(status().isOk());
    }

    // Helfermethode für Basic Auth Header
    private String basicAuth(String username, String password) {
        String auth = username + ":" + password;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(auth.getBytes());
    }

    @Test
    public void testLoadAndSavePersonsWithLimit() throws Exception {
        int limit = 1000;
        long start = System.currentTimeMillis();

        mockMvc.perform(get("/api/person")
                        .param("limit", String.valueOf(limit))
                        .param("save", "true")
                        .header("Authorization", basicAuth("admin", "testtest")))
                .andExpect(status().isOk());

        long duration = System.currentTimeMillis() - start;
        System.out.println("⏱ Testlauf (limit=" + limit + ") dauerte " + duration + " ms");
    }


}
