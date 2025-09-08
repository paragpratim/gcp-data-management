package org.fusadora.liquibase.services;

import org.fusadora.liquibase.repository.LiquibaseRepository;
import org.fusadora.liquibase.utils.LiquibaseChangeLogUtil;
import org.fusadora.liquibase.utils.LiquibaseChangeSetUtil;
import org.fusadora.liquibase.utils.LiquibaseCommandUtil;
import org.fusadora.model.datacontract.DataContract;
import org.fusadora.model.datacontract.PhysicalTable;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class LiquibaseServiceImpl implements LiquibaseService {

    private static final String LIQUIBASE_PATH = "liquibase";
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LiquibaseServiceImpl.class);

    @Autowired
    LiquibaseRepository liquibaseRepository;

    @Override
    public void generateChangeLog(String contractId) {
        Long id = Long.parseLong(contractId);
        Optional<DataContract> dataContract = liquibaseRepository.findById(id);
        if (dataContract.isPresent()) {
            // Generate the Liquibase Change Log JSON file
            try {
                LiquibaseChangeLogUtil.generateLiquibaseChangeLogJsonFile(dataContract.get().getBigQueryDataset().getProject(),
                        dataContract.get().getBigQueryDataset().getDataset(),
                        LIQUIBASE_PATH);
            } catch (IOException e) {
                logger.error("Error generating Change Log", e);
            }
            // Generate the Liquibase change set sql files
            try {
                for (PhysicalTable table : dataContract.get().getPhysicalModel().getPhysicalTables()) {
                    LiquibaseChangeSetUtil.generateLiquibaseChangeSetSqlFile(table,
                            dataContract.get().getBigQueryDataset().getProject(),
                            dataContract.get().getBigQueryDataset().getDataset(),
                            LIQUIBASE_PATH);
                }
            } catch (IOException e) {
                logger.error("Error generating Change Set SQL files", e);
            }
        } else {
            throw new IllegalArgumentException("Contract not found with id: " + contractId);
        }
    }

    @Override
    public void applyChangeLog(String contractId) {
        Long id = Long.parseLong(contractId);
        Optional<DataContract> dataContract = liquibaseRepository.findById(id);
        if (dataContract.isPresent()) {
            LiquibaseCommandUtil.updateBigQuery(dataContract.get().getBigQueryDataset().getProject(),
                    dataContract.get().getBigQueryDataset().getDataset(),
                    LIQUIBASE_PATH,
                    dataContract.get().getVersion());
        } else {
            throw new IllegalArgumentException("Contract not found with id: " + contractId);
        }
    }
}
