package com.fusadora.liquibase.utils;

import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * com.fusadora.liquibase.utils.LiquibaseChangeSetUtil
 * Utility class to generate Liquibase formatted SQL change sets for Physical Tables.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

public class LiquibaseChangeSetUtil {

    private static final String CHANGESET_AUTHOR = "fusadora";
    private static final String EMPTY_DESCRIPTION = "''";
    private static final String ALTER_TABLE_PREFIX = "ALTER TABLE ";
    private static final String ROLLBACK_ALTER_TABLE_PREFIX = "--rollback ALTER TABLE ";
    private static final String SET_OPTIONS_DESCRIPTION_PREFIX = " SET OPTIONS(description=";

    private LiquibaseChangeSetUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static String getEscapedDescriptionLiteral(String description) {
        if (description == null || description.isBlank()) {
            return EMPTY_DESCRIPTION;
        }
        return "'" + description.replace("'", "''") + "'";
    }

    private static String getInlineDescriptionOptions(String description) {
        if (description == null || description.isBlank()) {
            return "";
        }
        return " OPTIONS(description=" + getEscapedDescriptionLiteral(description) + ")";
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
            String changeSetSql;
            if (changeSetNumber == 1) {
                changeSetSql = getCreateTableStatement(aPhysicalTable, dataSetName);
            } else {
                changeSetSql = getAlterTableStatement(aPhysicalTable, dataSetName, changeSetNumber);
            }

            if (!changeSetSql.isBlank()) {
                changeSet.append(getChangesetHeader(aPhysicalTable, changeSetNumber));
                changeSet.append(changeSetSql);
            }

        }

        String descriptionSql = getDescriptionChangeSetSql(aPhysicalTable, dataSetName);
        if (!descriptionSql.isBlank()) {
            changeSet.append(getDescriptionChangesetHeader(aPhysicalTable));
            changeSet.append(descriptionSql);
        }
        return changeSet.toString();
    }

    private static String getTypeOrStructDefinition(PhysicalField field) {
        if (field == null || field.getType() == null) {
            return null;
        }

        if ("ARRAY".equalsIgnoreCase(field.getType())) {
            return getArrayDefinition(field);
        }

        List<String> nestedDefinitions = getNestedDefinitions(field.getNestedFields());
        if (!nestedDefinitions.isEmpty()) {
            return "STRUCT<" + String.join(", ", nestedDefinitions) + ">";
        }
        return field.getType();
    }

    private static String getArrayDefinition(PhysicalField field) {
        List<PhysicalField> nestedFields = field.getNestedFields();
        if (nestedFields == null || nestedFields.isEmpty()) {
            return field.getType();
        }

        // Allow primitive array representation with a single unnamed element definition.
        if (nestedFields.size() == 1) {
            PhysicalField elementField = nestedFields.get(0);
            if (elementField != null
                    && (elementField.getName() == null || elementField.getName().isBlank())
                    && elementField.getType() != null) {
                String elementType = getTypeOrStructDefinition(elementField);
                if (elementType != null) {
                    return "ARRAY<" + elementType + ">";
                }
            }
        }

        List<String> nestedDefinitions = getNestedDefinitions(nestedFields);
        if (!nestedDefinitions.isEmpty()) {
            return "ARRAY<STRUCT<" + String.join(", ", nestedDefinitions) + ">>";
        }
        return field.getType();
    }

    private static List<String> getNestedDefinitions(List<PhysicalField> fields) {
        List<String> nestedDefinitions = new ArrayList<>();
        if (fields == null || fields.isEmpty()) {
            return nestedDefinitions;
        }

        for (PhysicalField field : fields) {
            if (field == null || field.getName() == null) {
                continue;
            }

            String typeDefinition = getTypeOrStructDefinition(field);
            if (typeDefinition != null) {
                nestedDefinitions.add(field.getName() + " " + typeDefinition + getInlineDescriptionOptions(field.getDescription()));
            }
        }
        return nestedDefinitions;
    }

    private static List<String> getCreateColumnDefinitionsForChangeSet(PhysicalTable aPhysicalTable) {
        List<String> columnDefinitions = new ArrayList<>();
        if (aPhysicalTable.getPhysicalFields() == null) {
            return columnDefinitions;
        }

        for (PhysicalField field : aPhysicalTable.getPhysicalFields()) {
            if (field == null || field.getName() == null || field.getChangeSetNumber() != 1) {
                continue;
            }

            String typeDefinition = getTypeOrStructDefinition(field);
            if (typeDefinition != null) {
                columnDefinitions.add(field.getName() + " " + typeDefinition);
            }
        }
        return columnDefinitions;
    }

    private static void collectAlterColumnsRecursively(List<PhysicalField> fields,
                                                       int changeSetNumber,
                                                       String parentPath,
                                                       boolean parentAddedInChangeSet,
                                                       List<String> alterColumnDefinitions) {
        if (fields == null || fields.isEmpty()) {
            return;
        }

        for (PhysicalField field : fields) {
            if (field == null || field.getName() == null) {
                continue;
            }

            String qualifiedName = parentPath == null ? field.getName() : parentPath + "." + field.getName();
            boolean addedInThisChangeSet = false;
            if (!parentAddedInChangeSet && field.getChangeSetNumber() == changeSetNumber) {
                String typeDefinition = getTypeOrStructDefinition(field);
                if (typeDefinition != null) {
                    alterColumnDefinitions.add(qualifiedName + " " + typeDefinition);
                    addedInThisChangeSet = true;
                }
            }

            collectAlterColumnsRecursively(field.getNestedFields(), changeSetNumber, qualifiedName, parentAddedInChangeSet || addedInThisChangeSet, alterColumnDefinitions);
        }
    }

    private static List<String> getAlterColumnDefinitionsForChangeSet(PhysicalTable aPhysicalTable, int changeSetNumber) {
        List<String> alterColumnDefinitions = new ArrayList<>();
        collectAlterColumnsRecursively(aPhysicalTable.getPhysicalFields(), changeSetNumber, null, false, alterColumnDefinitions);
        return alterColumnDefinitions;
    }

    private static String extractColumnName(String alterColumnDefinition) {
        int firstSpaceIndex = alterColumnDefinition.indexOf(' ');
        if (firstSpaceIndex < 0) {
            return alterColumnDefinition;
        }
        return alterColumnDefinition.substring(0, firstSpaceIndex);
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
     * Generates a dedicated description changeset header, e.g. sales_order_1_1_desc.
     * Uses runOnChange:true so Liquibase re-applies this changeset whenever the
     * description text changes, without causing a checksum mismatch error.
     * The underlying ALTER TABLE SET OPTIONS is idempotent and safe to re-run.
     */
    private static String getDescriptionChangesetHeader(PhysicalTable aPhysicalTable) {
        return "--changeset " + CHANGESET_AUTHOR + ":" + aPhysicalTable.getName() + "_desc runOnChange:true" + System.lineSeparator();
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
        List<String> columnDefinitions = getCreateColumnDefinitionsForChangeSet(aPhysicalTable);
        changeSet.append("CREATE TABLE IF NOT EXISTS ")
                .append(dataSetName)
                .append(".")
                .append(aPhysicalTable.getName())
                .append(" (")
                .append(System.lineSeparator());
        //Columns
        for (String columnDefinition : columnDefinitions) {
            changeSet.append("    ")
                    .append(columnDefinition);
            changeSet.append(",")
                    .append(System.lineSeparator());
        }
        //Remove last comma
        if (!columnDefinitions.isEmpty()) {
            changeSet.setLength(changeSet.length() - 2);
        }
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

    private static void collectTopLevelColumnDescriptionStatements(List<PhysicalField> fields,
                                                                   String dataSetName,
                                                                   String tableName,
                                                                   List<String> statements,
                                                                   List<String> rollbacks) {
        if (fields == null || fields.isEmpty()) {
            return;
        }

        for (PhysicalField field : fields) {
            if (field == null || field.getName() == null || field.getName().isBlank()) {
                continue;
            }

            statements.add(ALTER_TABLE_PREFIX + dataSetName + "." + tableName
                    + " ALTER COLUMN " + field.getName()
                    + SET_OPTIONS_DESCRIPTION_PREFIX + getEscapedDescriptionLiteral(field.getDescription()) + ");");
            rollbacks.add(ROLLBACK_ALTER_TABLE_PREFIX + dataSetName + "." + tableName
                    + " ALTER COLUMN " + field.getName() + SET_OPTIONS_DESCRIPTION_PREFIX + EMPTY_DESCRIPTION + ");");
        }
    }

    private static String getDescriptionChangeSetSql(PhysicalTable aPhysicalTable, String dataSetName) {
        List<String> statements = new ArrayList<>();
        List<String> rollbacks = new ArrayList<>();

        statements.add(ALTER_TABLE_PREFIX + dataSetName + "." + aPhysicalTable.getName()
                + SET_OPTIONS_DESCRIPTION_PREFIX + getEscapedDescriptionLiteral(aPhysicalTable.getDescription()) + ");");
        rollbacks.add(ROLLBACK_ALTER_TABLE_PREFIX + dataSetName + "." + aPhysicalTable.getName()
                + SET_OPTIONS_DESCRIPTION_PREFIX + EMPTY_DESCRIPTION + ");");

        collectTopLevelColumnDescriptionStatements(aPhysicalTable.getPhysicalFields(), dataSetName, aPhysicalTable.getName(), statements, rollbacks);

        StringBuilder changeSet = new StringBuilder();
        for (String statement : statements) {
            changeSet.append(statement).append(System.lineSeparator());
        }
        for (String rollback : rollbacks) {
            changeSet.append(rollback).append(System.lineSeparator());
        }
        changeSet.append(System.lineSeparator());
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
        List<String> alterColumnDefinitions = getAlterColumnDefinitionsForChangeSet(aPhysicalTable, thisChangeSet);
        if (alterColumnDefinitions.isEmpty()) {
            return "";
        }

        StringBuilder changeSet = new StringBuilder();
        changeSet.append(ALTER_TABLE_PREFIX)
                .append(dataSetName)
                .append(".")
                .append(aPhysicalTable.getName())
                .append(System.lineSeparator());
        //Columns
        for (String columnDefinition : alterColumnDefinitions) {
            changeSet.append("    ADD COLUMN IF NOT EXISTS ")
                    .append(columnDefinition);
            changeSet.append(",")
                    .append(System.lineSeparator());
        }
        //Remove last comma
        changeSet.setLength(changeSet.length() - 2);
        changeSet.append(System.lineSeparator())
                .append(";")
                .append(System.lineSeparator());


        //Rollback for alter table
        for (String columnDefinition : alterColumnDefinitions) {
            changeSet.append(ROLLBACK_ALTER_TABLE_PREFIX)
                    .append(dataSetName)
                    .append(".")
                    .append(aPhysicalTable.getName())
                    .append(" DROP COLUMN IF EXISTS ")
                    .append(extractColumnName(columnDefinition))
                    .append(";")
                    .append(System.lineSeparator());
        }
        changeSet.append(System.lineSeparator());
        return changeSet.toString();
    }
}
