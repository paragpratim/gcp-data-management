package com.fusadora.liquibase.utils.changeset;

import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalTable;

import java.util.ArrayList;
import java.util.List;

/**
 * com.fusadora.liquibase.utils.changeset.ChangeSetSqlBuilder
 * Assembles all Liquibase-formatted SQL blocks for a PhysicalTable:
 * CREATE TABLE, ALTER TABLE, and the mutable description changeset.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
class ChangeSetSqlBuilder {

    private ChangeSetSqlBuilder() {
    }

    /**
     * Builds the complete Liquibase SQL content for a table across all its changesets.
     *
     * @param table       the PhysicalTable for which to build the changesets
     * @param dataSetName the name of the dataset/schema to which the table belongs
     * @return a String containing the full Liquibase-formatted SQL for all changesets of the table
     */
    static String buildFullChangeSet(PhysicalTable table, String dataSetName) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildLiquibaseHeader()).append(System.lineSeparator());

        for (int changeSetNumber = 1; changeSetNumber <= table.getCurrentChangeSetNumber(); changeSetNumber++) {
            String changeSetSql = changeSetNumber == 1
                    ? buildCreateTableSql(table, dataSetName)
                    : buildAlterTableSql(table, dataSetName, changeSetNumber);

            if (!changeSetSql.isBlank()) {
                sb.append(buildChangeSetHeader(table, changeSetNumber));
                sb.append(changeSetSql);
            }
        }

        sb.append(buildDescriptionChangeSetHeader(table));
        sb.append(buildDescriptionSql(table, dataSetName));
        return sb.toString();
    }

    /**
     * Builds the CREATE TABLE SQL block for the initial changeset of a PhysicalTable.
     *
     * @param table       the PhysicalTable for which to build the CREATE TABLE statement
     * @param dataSetName the name of the dataset/schema to which the table belongs
     * @return a String containing the CREATE TABLE statement with appropriate column definitions and rollback
     */
    static String buildCreateTableSql(PhysicalTable table, String dataSetName) {
        List<String> columnDefinitions = ChangeSetColumnCollector.getCreateColumnDefinitions(table);

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(dataSetName).append(".").append(table.getName())
                .append(" (").append(System.lineSeparator());

        for (String columnDefinition : columnDefinitions) {
            sb.append("    ").append(columnDefinition).append(",").append(System.lineSeparator());
        }
        if (!columnDefinitions.isEmpty()) {
            sb.setLength(sb.length() - 2); // remove trailing comma + newline
        }
        sb.append(System.lineSeparator()).append(");").append(System.lineSeparator());
        sb.append("--rollback DROP TABLE IF EXISTS ")
                .append(dataSetName).append(".").append(table.getName()).append(";")
                .append(System.lineSeparator()).append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * Builds the ALTER TABLE SQL block for subsequent changesets of a PhysicalTable, adding new columns as needed.
     *
     * @param table           the PhysicalTable for which to build the ALTER TABLE statement
     * @param dataSetName     the name of the dataset/schema to which the table belongs
     * @param changeSetNumber the specific changeset number for which to build the ALTER TABLE statement
     * @return a String containing the ALTER TABLE statement with appropriate ADD COLUMN definitions and corresponding rollbacks
     */
    static String buildAlterTableSql(PhysicalTable table, String dataSetName, int changeSetNumber) {
        List<String> alterColumnDefinitions =
                ChangeSetColumnCollector.getAlterColumnDefinitions(table, changeSetNumber);
        if (alterColumnDefinitions.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(LiquibaseChangeSetConstants.ALTER_TABLE_PREFIX)
                .append(dataSetName).append(".").append(table.getName())
                .append(System.lineSeparator());

        for (String columnDefinition : alterColumnDefinitions) {
            sb.append("    ADD COLUMN IF NOT EXISTS ").append(columnDefinition)
                    .append(",").append(System.lineSeparator());
        }
        sb.setLength(sb.length() - 2); // remove trailing comma + newline
        sb.append(System.lineSeparator()).append(";").append(System.lineSeparator());

        for (String columnDefinition : alterColumnDefinitions) {
            sb.append(LiquibaseChangeSetConstants.ROLLBACK_ALTER_TABLE_PREFIX)
                    .append(dataSetName).append(".").append(table.getName())
                    .append(" DROP COLUMN IF EXISTS ")
                    .append(ChangeSetColumnCollector.extractTopLevelColumnName(columnDefinition))
                    .append(";").append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * Builds the SQL block for setting descriptions on the table and its top-level columns, along with corresponding rollbacks.
     *
     * @param table       the PhysicalTable for which to build the description ALTER TABLE statements
     * @param dataSetName the name of the dataset/schema to which the table belongs
     * @return a String containing the ALTER TABLE statements to set descriptions on the table and its top-level columns, along with rollbacks to clear those descriptions
     */
    static String buildDescriptionSql(PhysicalTable table, String dataSetName) {
        List<String> statements = new ArrayList<>();
        List<String> rollbacks = new ArrayList<>();

        statements.add(LiquibaseChangeSetConstants.ALTER_TABLE_PREFIX
                + dataSetName + "." + table.getName()
                + LiquibaseChangeSetConstants.SET_OPTIONS_DESCRIPTION_PREFIX
                + LiquibaseChangeSetConstants.getEscapedDescriptionLiteral(table.getDescription()) + ");");
        rollbacks.add(LiquibaseChangeSetConstants.ROLLBACK_ALTER_TABLE_PREFIX
                + dataSetName + "." + table.getName()
                + LiquibaseChangeSetConstants.SET_OPTIONS_DESCRIPTION_PREFIX
                + LiquibaseChangeSetConstants.EMPTY_DESCRIPTION + ");");

        collectTopLevelColumnDescriptionStatements(
                table.getPhysicalFields(), dataSetName, table.getName(), statements, rollbacks);

        StringBuilder sb = new StringBuilder();
        statements.forEach(s -> sb.append(s).append(System.lineSeparator()));
        rollbacks.forEach(r -> sb.append(r).append(System.lineSeparator()));
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * Recursively collects ALTER TABLE statements to set descriptions on top-level columns of a PhysicalTable, along with corresponding rollbacks.
     *
     * @param fields      the list of PhysicalFields to process for description statements
     * @param dataSetName the name of the dataset/schema to which the table belongs
     * @param tableName   the name of the table to which the columns belong
     * @param statements  the list to which generated ALTER TABLE statements for setting descriptions will be added
     * @param rollbacks   the list to which generated ALTER TABLE statements for rolling back descriptions will be added
     */
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

            statements.add(LiquibaseChangeSetConstants.ALTER_TABLE_PREFIX
                    + dataSetName + "." + tableName
                    + " ALTER COLUMN " + field.getName()
                    + LiquibaseChangeSetConstants.SET_OPTIONS_DESCRIPTION_PREFIX
                    + LiquibaseChangeSetConstants.getEscapedDescriptionLiteral(field.getDescription()) + ");");
            rollbacks.add(LiquibaseChangeSetConstants.ROLLBACK_ALTER_TABLE_PREFIX
                    + dataSetName + "." + tableName
                    + " ALTER COLUMN " + field.getName()
                    + LiquibaseChangeSetConstants.SET_OPTIONS_DESCRIPTION_PREFIX
                    + LiquibaseChangeSetConstants.EMPTY_DESCRIPTION + ");");
        }
    }

    /**
     * Builds the standard Liquibase header comment that indicates the file is formatted for Liquibase.
     *
     * @return a String containing the Liquibase header comment
     */
    private static String buildLiquibaseHeader() {
        return "--liquibase formatted sql" + System.lineSeparator();
    }

    /**
     * Builds the header comment for a specific changeset, including the author and a unique identifier based on the table name and changeset number.
     *
     * @param table           the PhysicalTable for which to build the changeset header
     * @param changeSetNumber the specific changeset number to include in the header for uniqueness
     * @return a String containing the changeset header comment for the specified changeset number of the table
     */
    private static String buildChangeSetHeader(PhysicalTable table, int changeSetNumber) {
        return "--changeset " + LiquibaseChangeSetConstants.CHANGESET_AUTHOR
                + ":" + table.getName() + "_" + changeSetNumber + System.lineSeparator();
    }

    private static String buildDescriptionChangeSetHeader(PhysicalTable table) {
        return "--changeset " + LiquibaseChangeSetConstants.CHANGESET_AUTHOR
                + ":" + table.getName() + "_desc runOnChange:true" + System.lineSeparator();
    }
}


