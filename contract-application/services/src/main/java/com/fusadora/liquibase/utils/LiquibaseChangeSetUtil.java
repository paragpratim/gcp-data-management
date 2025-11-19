package com.fusadora.liquibase.utils;

import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * com.fusadora.liquibase.utils.LiquibaseChangeSetUtil
 * Utility class to generate Liquibase formatted SQL change sets for Physical Tables.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

public class LiquibaseChangeSetUtil {

    private static final String CHANGESET_AUTHOR = "fusadora";

    private LiquibaseChangeSetUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a Liquibase formatted SQL file for the given PhysicalTable.
     *
     * @param aPhysicalTable   The PhysicalTable object containing table details.
     * @param aProjectId       The project ID for directory structure.
     * @param aDatasetName     The dataset name where the table resides.
     * @param liquibasePath    The base path to store the generated SQL file.
     * @param dataProductVersion The version of the data product for directory structure.
     * @throws IOException If an I/O error occurs writing to or creating the file.
     */
    public static void generateLiquibaseChangeSetSqlFile(PhysicalTable aPhysicalTable, String aProjectId, String aDatasetName, String liquibasePath, String dataProductVersion) throws IOException {
        String liquibaseFormatedSql = getLiquibaseChangeSetSql(aPhysicalTable, aDatasetName);
        // File Path: <liquibasePath>/<projectId>/<dataProductVersion>/<dataSetName>/<tableName>.sql
        Path changeSetDirectoryPath = Path.of(liquibasePath).resolve(aProjectId).resolve(dataProductVersion).resolve(aDatasetName);
        Files.createDirectories(changeSetDirectoryPath);
        Files.writeString(changeSetDirectoryPath.resolve(aPhysicalTable.getName() + ".sql"), liquibaseFormatedSql);
    }

    /**
     * Generates Liquibase formatted SQL change set for the given PhysicalTable.
     *
     * @param aPhysicalTable The PhysicalTable object containing table details.
     * @param dataSetName    The dataset name where the table resides.
     * @return A string containing the Liquibase formatted SQL change set.
     */
    public static String getLiquibaseChangeSetSql(PhysicalTable aPhysicalTable, String dataSetName) {
        StringBuilder changeSet = new StringBuilder();

        //Liquibase header
        changeSet.append(getLiquibaseHeader());
        changeSet.append(System.lineSeparator());

        //Iterate through changesets
        for (int changeSetNumber = 1; changeSetNumber <= aPhysicalTable.getCurrentChangeSetNumber(); changeSetNumber++) {
            // Logic to generate SQL for each changeset
            // header
            changeSet.append(getChangesetHeader(aPhysicalTable, changeSetNumber));
            // For the first changeset, create table statement
            if (changeSetNumber == 1) {
                //Create Table statement
                changeSet.append(getCreateTableStatement(aPhysicalTable, dataSetName));
            } else if (changeSetNumber > 1) {
                // Future changesets handled here (e.g., ALTER TABLE statements)
                changeSet.append(getAlterTableStatement(aPhysicalTable, dataSetName, changeSetNumber));
            }
        }
        return changeSet.toString();
    }


    /**
     * Generates the Liquibase header for the SQL file.
     *
     * @return A string containing the Liquibase header.
     */
    private static String getLiquibaseHeader() {
        return "--liquibase formatted sql" + System.lineSeparator();
    }

    /**
     * Generates the changeset header for a specific changeset number.
     *
     * @param aPhysicalTable  The PhysicalTable object containing table details.
     * @param changeSetNumber The changeset number.
     * @return A string containing the changeset header.
     */
    private static String getChangesetHeader(PhysicalTable aPhysicalTable, int changeSetNumber) {
        return "--changeset " + CHANGESET_AUTHOR + ":" + aPhysicalTable.getName() + "_" + changeSetNumber + System.lineSeparator();
    }

    /**
     * Generates the CREATE TABLE statement for the first changeset.
     *
     * @param aPhysicalTable The PhysicalTable object containing table details.
     * @param dataSetName    The dataset name where the table resides.
     * @return A string containing the CREATE TABLE statement.
     */
    private static String getCreateTableStatement(PhysicalTable aPhysicalTable, String dataSetName) {
        StringBuilder changeSet = new StringBuilder();
        changeSet.append("CREATE TABLE IF NOT EXISTS ")
                .append(dataSetName)
                .append(".")
                .append(aPhysicalTable.getName())
                .append(" (")
                .append(System.lineSeparator());
        //Columns
        for (PhysicalField field : aPhysicalTable.getPhysicalFields()) {
            if (field.getChangeSetNumber() != 1) {
                continue;
            }
            changeSet.append("    ")
                    .append(field.getName())
                    .append(" ")
                    .append(field.getType());

            changeSet.append(",")
                    .append(System.lineSeparator());
        }
        //Remove last comma
        changeSet.setLength(changeSet.length() - 2);
        changeSet.append(System.lineSeparator())
                .append(");")
                .append(System.lineSeparator());
        //Rollback for create table
        changeSet.append("--rollback DROP TABLE IF EXISTS ")
                .append(dataSetName)
                .append(".")
                .append(aPhysicalTable.getName())
                .append(";")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        return changeSet.toString();
    }

    /**
     * Generates the ALTER TABLE statement for subsequent changesets.
     *
     * @param aPhysicalTable The PhysicalTable object containing table details.
     * @param dataSetName    The dataset name where the table resides.
     * @param thisChangeSet  The current changeset number.
     * @return A string containing the ALTER TABLE statement.
     */
    private static String getAlterTableStatement(PhysicalTable aPhysicalTable, String dataSetName, int thisChangeSet) {
        StringBuilder changeSet = new StringBuilder();
        changeSet.append("ALTER TABLE ")
                .append(dataSetName)
                .append(".")
                .append(aPhysicalTable.getName())
                .append(System.lineSeparator());
        //Columns
        for (PhysicalField field : aPhysicalTable.getPhysicalFields()) {
            if (field.getChangeSetNumber() != thisChangeSet) {
                continue;
            }
            changeSet.append("    ADD COLUMN IF NOT EXISTS ")
                    .append(field.getName())
                    .append(" ")
                    .append(field.getType());
            changeSet.append(",")
                    .append(System.lineSeparator());
        }
        //Remove last comma
        changeSet.setLength(changeSet.length() - 2);
        changeSet.append(System.lineSeparator())
                .append(";")
                .append(System.lineSeparator());
        //Rollback for alter table
        for (PhysicalField field : aPhysicalTable.getPhysicalFields()) {
            if (field.getChangeSetNumber() != thisChangeSet) {
                continue;
            }
            changeSet.append("--rollback ALTER TABLE ")
                    .append(dataSetName)
                    .append(".")
                    .append(aPhysicalTable.getName())
                    .append(" DROP COLUMN IF EXISTS ")
                    .append(field.getName())
                    .append(";")
                    .append(System.lineSeparator());
        }
        changeSet.append(System.lineSeparator());
        return changeSet.toString();
    }
}
