package com.fusadora.liquibase.controller;

import com.fusadora.liquibase.services.LiquibaseService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/liquibase")
@CrossOrigin(origins = "*")
public class LiquibaseController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(LiquibaseController.class);

    @Autowired
    private LiquibaseService liquibaseService;

    @PostMapping("/generateChangelog/{contractId}")
    public String generateChangelog(@PathVariable String contractId) {
        return liquibaseService.generateChangeLog(contractId);
    }

    @PostMapping("/applyChangelog/{contractId}")
    public String applyChangelog(@PathVariable String contractId) {
        return liquibaseService.applyChangeLog(contractId);
    }
}
