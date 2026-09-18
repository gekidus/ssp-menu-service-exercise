package com.ssp.menuservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsMenuForKnownUnit() throws Exception {
        mockMvc.perform(get("/menu/LHR-T5-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Bacon roll")))
                .andExpect(jsonPath("$[1]", is("Flat white")))
                .andExpect(jsonPath("$[2]", is("Orange juice")));
    }

    @Test
    void returns404ForUnknownUnit() throws Exception {
        mockMvc.perform(get("/menu/DOES-NOT-EXIST"))
                .andExpect(status().isNotFound());
    }

    @Test
    void healthEndpointReturns200() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }
}