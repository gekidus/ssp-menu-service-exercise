package com.ssp.menuservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "menu.database.up=false")
class MenuDatabaseOutageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returns500WhenMenuDatabaseIsUnavailable() throws Exception {
        mockMvc.perform(get("/menu/LHR-T5-001"))
                .andExpect(status().isInternalServerError());
    }
}