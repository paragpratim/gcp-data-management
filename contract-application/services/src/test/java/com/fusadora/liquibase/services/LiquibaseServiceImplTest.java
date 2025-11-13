package com.fusadora.liquibase.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LiquibaseServiceImplTest {

    @Test
    void testLiquibasePath() {
        LiquibaseServiceImpl service = new LiquibaseServiceImpl();
        String contractId = "123";
        String expectedPath = "/mnt/liquibase/123";
        String actualPath = service.liquibasePathFor(contractId);
        assertEquals(expectedPath, actualPath);
    }
}