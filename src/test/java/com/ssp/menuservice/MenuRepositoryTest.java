package com.ssp.menuservice;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MenuRepositoryTest {

    @Test
    void throwsExceptionWhenDatabaseIsUnavailable() {
        MenuRepository repository = new MenuRepository();

        ReflectionTestUtils.setField(repository, "databaseUp", false);

        assertThrows(
                IllegalStateException.class,
                () -> repository.findMenu("LHR-T5-001")
        );
    }
}