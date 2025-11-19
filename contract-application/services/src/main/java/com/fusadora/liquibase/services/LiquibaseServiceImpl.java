package com.fusadora.liquibase.services;

import com.fusadora.liquibase.repository.LiquibaseRepository;
import com.fusadora.liquibase.utils.LiquibaseChangeLogUtil;
import com.fusadora.liquibase.utils.LiquibaseChangeSetUtil;
import com.fusadora.liquibase.utils.LiquibaseCommandUtil;
import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalTable;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * com.fusadora.liquibase.services.LiquibaseServiceImpl
 * Implementation of LiquibaseService to handle Liquibase operations for Data Contracts.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

@Service
public class LiquibaseServiceImpl implements LiquibaseService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LiquibaseServiceImpl.class);
    @Autowired
    LiquibaseRepository liquibaseRepository;
    @Value("${liquibase.change.path:/mnt/liquibase}")
    private String liquibaseChangePath;
    @Value("${liquibase.change.dataset:liquibase_admin}")
    private String liquibaseChangeDataset;

    /**
     * Generates Liquibase Change Log and Change Set SQL files for the specified Data Contract.
     *
     * @param contractId The ID of the Data Contract.
     * @return The path to the generated Liquibase Change Log directory.
     */
    @Override
    public String generateChangeLog(String contractId) {
        Long id = Long.parseLong(contractId);
        String liquibaseDirectoryPath = null;
        Optional<DataContract> dataContract = liquibaseRepository.findById(id);
        if (dataContract.isPresent()) {
            // Generate the Liquibase Change Log JSON file
            try {
                liquibaseDirectoryPath = LiquibaseChangeLogUtil.generateLiquibaseChangeLogJsonFile(dataContract.get().getBigQueryDataset().getProject(),
                        dataContract.get().getBigQueryDataset().getDataset(),
                        liquibasePathFor(contractId),
                        dataContract.get().getVersion().toString());
            } catch (IOException e) {
                logger.error("Error generating Change Log", e);
            }
            // Generate the Liquibase change set sql files
            try {
                for (PhysicalTable table : dataContract.get().getPhysicalModel().getPhysicalTables()) {
                    LiquibaseChangeSetUtil.generateLiquibaseChangeSetSqlFile(table,
                            dataContract.get().getBigQueryDataset().getProject(),
                            dataContract.get().getBigQueryDataset().getDataset(),
                            liquibasePathFor(contractId),
                            dataContract.get().getVersion().toString());
                }
            } catch (IOException e) {
                logger.error("Error generating Change Set SQL files", e);
            }
        } else {
            throw new IllegalArgumentException("Contract not found with id: " + contractId);
        }
        return liquibaseDirectoryPath;
    }

    /**
     * Applies the Liquibase Change Log to the BigQuery dataset for the specified Data Contract.
     *
     * @param contractId The ID of the Data Contract.
     * @return The response from the Liquibase update command.
     */
    @Override
    public String applyChangeLog(String contractId) {
        Long id = Long.parseLong(contractId);
        Optional<DataContract> dataContract = liquibaseRepository.findById(id);
        String response;
        if (dataContract.isPresent()) {
            response = LiquibaseCommandUtil.updateBigQuery(dataContract.get().getBigQueryDataset().getProject(),
                    dataContract.get().getBigQueryDataset().getDataset(),
                    liquibasePathFor(contractId),
                    dataContract.get().getVersion().toString(), contractId, liquibaseChangeDataset);
        } else {
            throw new IllegalArgumentException("Contract not found with id: " + contractId);
        }
        return response;
    }

    /** Helper method to construct the Liquibase path for a given contract ID
     *
     * @param contractId The ID of the Data Contract.
     * @return The constructed Liquibase path as a String.
     */
    String liquibasePathFor(String contractId) {
        return Paths.get(liquibaseChangePath, contractId).toString();
    }
}
