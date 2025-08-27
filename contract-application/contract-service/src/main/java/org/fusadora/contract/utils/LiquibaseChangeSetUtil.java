package org.fusadora.contract.utils;

import org.fusadora.model.datacontract.PhysicalField;
import org.fusadora.model.datacontract.PhysicalTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LiquibaseChangeSetUtil {

    private static final String CHANGESET_AUTHOR = "fusadora";

    private LiquibaseChangeSetUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void generateLiquibaseChangeSetSqlFile(PhysicalTable aPhysicalTable, String dataSetName, String filePath) throws IOException {
        String liquibaseFormatedSql = getLiquibaseChangeSetSql(aPhysicalTable, dataSetName);
        Path changeSetDirectoryPath = Path.of(filePath).resolve(dataSetName);
        Files.createDirectories(changeSetDirectoryPath);
        Files.writeString(changeSetDirectoryPath.resolve(aPhysicalTable.getName() + ".sql"), liquibaseFormatedSql);
    }

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
            } else {
                // Future changesets handled here (e.g., ALTER TABLE statements)
                changeSet.append(getAlterTableStatement(aPhysicalTable, dataSetName));
            }
        }
        return changeSet.toString();
    }


    private static String getLiquibaseHeader() {
        return "--liquibase formatted sql" + System.lineSeparator();
    }

    private static String getChangesetHeader(PhysicalTable aPhysicalTable, int changeSetNumber) {
        return "--changeset " + CHANGESET_AUTHOR + ":" + aPhysicalTable.getName() + "_" + changeSetNumber + System.lineSeparator();
    }

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

    private static String getAlterTableStatement(PhysicalTable aPhysicalTable, String dataSetName) {
        StringBuilder changeSet = new StringBuilder();
        changeSet.append("ALTER TABLE ")
                .append(dataSetName)
                .append(".")
                .append(aPhysicalTable.getName())
                .append(System.lineSeparator());
        //Columns
        for (PhysicalField field : aPhysicalTable.getPhysicalFields()) {
            changeSet.append("    ADD COLUMN IF NOT EXISTS ")
                    .append(field.getName())
                    .append(" ")
                    .append(field.getType());
            changeSet.append(",")
                    .append(System.lineSeparator());
            //Remove last comma
            changeSet.setLength(changeSet.length() - 2);
            changeSet.append(System.lineSeparator())
                    .append(";")
                    .append(System.lineSeparator());
        }
        //Rollback for alter table
        for (PhysicalField field : aPhysicalTable.getPhysicalFields()) {
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
