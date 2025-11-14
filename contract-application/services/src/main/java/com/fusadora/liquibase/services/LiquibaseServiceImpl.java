package com.fusadora.liquibase.services;

import com.fusadora.liquibase.repository.LiquibaseRepository;
import com.fusadora.liquibase.utils.LiquibaseChangeLogUtil;
import com.fusadora.liquibase.utils.LiquibaseChangeSetUtil;
import com.fusadora.liquibase.utils.LiquibaseCommandUtil;
import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalTable;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class LiquibaseServiceImpl implements LiquibaseService {

    private static final String LIQUIBASE_PATH = "/mnt/liquibase";
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LiquibaseServiceImpl.class);

    @Autowired
    LiquibaseRepository liquibaseRepository;

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

    @Override
    public String applyChangeLog(String contractId) {
        Long id = Long.parseLong(contractId);
        Optional<DataContract> dataContract = liquibaseRepository.findById(id);
        String response;
        if (dataContract.isPresent()) {
            response = LiquibaseCommandUtil.updateBigQuery(dataContract.get().getBigQueryDataset().getProject(),
                    dataContract.get().getBigQueryDataset().getDataset(),
                    liquibasePathFor(contractId),
                    dataContract.get().getVersion().toString(), contractId);
        } else {
            throw new IllegalArgumentException("Contract not found with id: " + contractId);
        }
        return response;
    }

    String liquibasePathFor(String contractId) {
        return Paths.get(LIQUIBASE_PATH, contractId).toString();
    }
}
