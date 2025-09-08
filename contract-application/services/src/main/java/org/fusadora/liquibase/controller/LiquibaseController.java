package org.fusadora.liquibase.controller;

import org.fusadora.liquibase.services.LiquibaseService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/liquibase")
@CrossOrigin(origins = "*")
public class LiquibaseController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(LiquibaseController.class);

    @Autowired
    private LiquibaseService liquibaseService;

    @PostMapping("/generateChangelog/{contractId}")
    public ResponseEntity<Void> generateChangelog(@PathVariable String contractId) {
        liquibaseService.generateChangeLog(contractId);
        // TODO: return the changelog file or its path in the response
        return ResponseEntity.ok().build();
    }

    @PostMapping("/applyChangelog/{contractId}")
    public ResponseEntity<Void> applyChangelog(@PathVariable String contractId) {
        liquibaseService.applyChangeLog(contractId);
        // TODO: return the result of the operation in the response
        return ResponseEntity.ok().build();
    }
}
