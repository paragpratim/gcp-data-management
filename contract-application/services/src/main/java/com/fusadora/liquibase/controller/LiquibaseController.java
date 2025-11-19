package com.fusadora.liquibase.controller;

import com.fusadora.liquibase.services.LiquibaseService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * com.fusadora.liquibase.controller.LiquibaseController
 * This controller handles HTTP requests related to Liquibase operations such as generating and applying changelogs.
 * It exposes endpoints for generating and applying changelogs based on a given contract ID.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

@RestController
@RequestMapping("/api/liquibase")
@CrossOrigin(origins = "*")
public class LiquibaseController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(LiquibaseController.class);

    @Autowired
    private LiquibaseService liquibaseService;

    /**
     * Endpoint to generate a changelog for a given contract ID.
     *
     * @param contractId The ID of the contract for which to generate the changelog.
     * @return A string message indicating the result of the operation.
     */
    @PostMapping("/generateChangelog/{contractId}")
    public String generateChangelog(@PathVariable String contractId) {
        logger.info("Generating changelog for contract ID: {}", contractId);
        return liquibaseService.generateChangeLog(contractId);
    }

    /**
     * Endpoint to apply a changelog for a given contract ID.
     *
     * @param contractId The ID of the contract for which to apply the changelog.
     * @return A string message indicating the result of the operation.
     */
    @PostMapping("/applyChangelog/{contractId}")
    public String applyChangelog(@PathVariable String contractId) {
        logger.info("Applying changelog for contract ID: {}", contractId);
        return liquibaseService.applyChangeLog(contractId);
    }
}
