package com.fusadora.liquibase.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LiquibaseServiceImplTest {

    @Autowired
    private LiquibaseServiceImpl service;

    @Test
    void testLiquibasePath() {
        String contractId = "123";
        String expectedPath = "/mnt/liquibase/123";
        String actualPath = service.liquibasePathFor(contractId);
        assertEquals(expectedPath, actualPath);
    }
}