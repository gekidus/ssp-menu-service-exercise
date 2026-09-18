package com.ssp.menuservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "menu.database.up=false")
class MenuDatabaseOutageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void throwsExceptionWhenMenuDatabaseIsUnavailable() {
        assertThrows(
                Exception.class,
                () -> mockMvc.perform(get("/menu/LHR-T5-001"))
        );
    }
}