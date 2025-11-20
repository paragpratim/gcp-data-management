package com.fusadora;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ContractApplicationTests {

    @Autowired
    private ApplicationContext ctx;

    @Test
    void applicationContextStarts() {
        assertNotNull(ctx, "ApplicationContext should have been initialized");
    }

    @Test
    void contractApplicationBeanPresent() {
        assertNotNull(ctx.getBean(ContractApplication.class), "ContractApplication bean should be present in the context");
    }

}
