package com.fusadora.liquibase.utils.changeset;

import com.fusadora.model.datacontract.PhysicalTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * com.fusadora.liquibase.utils.LiquibaseChangeSetUtil
 * Public entry point for generating Liquibase formatted SQL change sets for Physical Tables.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */
public class LiquibaseChangeSetUtil {

    private LiquibaseChangeSetUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a Liquibase formatted SQL file for the given PhysicalTable.
     *
     * @param aPhysicalTable     The PhysicalTable object containing table details.
     * @param aProjectId         The project ID for directory structure.
     * @param aDatasetName       The dataset name where the table resides.
     * @param liquibasePath      The base path to store the generated SQL file.
     * @param dataProductVersion The version of the data product for directory structure.
     * @throws IOException If an I/O error occurs writing to or creating the file.
     */
    public static void generateLiquibaseChangeSetSqlFile(PhysicalTable aPhysicalTable,
                                                         String aProjectId,
                                                         String aDatasetName,
                                                         String liquibasePath,
                                                         String dataProductVersion) throws IOException {
        String sql = getLiquibaseChangeSetSql(aPhysicalTable, aDatasetName);
        // File Path: <liquibasePath>/<projectId>/<dataProductVersion>/<dataSetName>/<tableName>.sql
        Path changeSetDirectoryPath = Path.of(liquibasePath)
                .resolve(aProjectId)
                .resolve(dataProductVersion)
                .resolve(aDatasetName);
        Files.createDirectories(changeSetDirectoryPath);
        Files.writeString(changeSetDirectoryPath.resolve(aPhysicalTable.getName() + ".sql"), sql);
    }

    /**
     * Generates Liquibase formatted SQL change sets for the given PhysicalTable.
     *
     * @param aPhysicalTable The PhysicalTable object containing table details.
     * @param dataSetName    The dataset name where the table resides.
     * @return A string containing the complete Liquibase formatted SQL.
     */
    public static String getLiquibaseChangeSetSql(PhysicalTable aPhysicalTable, String dataSetName) {
        return ChangeSetSqlBuilder.buildFullChangeSet(aPhysicalTable, dataSetName);
    }
}

