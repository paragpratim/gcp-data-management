package com.fusadora.liquibase.utils.changeset;

import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalTable;

import java.util.ArrayList;
import java.util.List;

/**
 * com.fusadora.liquibase.utils.changeset.ChangeSetColumnCollector
 * Collects the column definitions that belong to a specific changeset number,
 * for use in CREATE TABLE and ALTER TABLE statements.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
class ChangeSetColumnCollector {

    private ChangeSetColumnCollector() {
    }

    /**
     * Returns the column definitions (name + type) for all fields that belong to
     * changeset 1, i.e. the initial CREATE TABLE statement.
     *
     * @param table the PhysicalTable containing the fields to extract column definitions from
     * @return a list of column definitions in the format "columnName typeDefinition" for fields in changeset 1
     */
    static List<String> getCreateColumnDefinitions(PhysicalTable table) {
        List<String> columnDefinitions = new ArrayList<>();
        if (table.getPhysicalFields() == null) {
            return columnDefinitions;
        }

        for (PhysicalField field : table.getPhysicalFields()) {
            if (field == null || field.getName() == null || field.getChangeSetNumber() != 1) {
                continue;
            }

            String typeDefinition = BigQueryTypeDefinitionBuilder.getTypeDefinition(field);
            if (typeDefinition != null) {
                columnDefinitions.add(field.getName() + " " + typeDefinition);
            }
        }
        return columnDefinitions;
    }

    /**
     * Returns the column definitions (qualified-name + type) for all fields that
     * belong to the given changeSetNumber, used in ALTER TABLE statements.
     * Nested fields whose parent was added in the same changeset are excluded
     * since they are embedded inside the parent STRUCT definition.
     *
     * @param table           the PhysicalTable containing the fields to extract column definitions from
     * @param changeSetNumber the changeset number to filter fields by
     * @return a list of column definitions in the format "qualifiedColumnName typeDefinition" for fields in the specified changeset
     */
    static List<String> getAlterColumnDefinitions(PhysicalTable table, int changeSetNumber) {
        List<String> alterColumnDefinitions = new ArrayList<>();
        collectAlterColumnsRecursively(
                table.getPhysicalFields(), changeSetNumber, null, false, alterColumnDefinitions);
        return alterColumnDefinitions;
    }

    /**
     * Recursively traverses the list of fields to collect column definitions for fields that belong to the specified changeset.
     *
     * @param fields                 the list of PhysicalField objects to traverse
     * @param changeSetNumber        the changeset number to filter fields by
     * @param parentPath             the qualified name path of the parent field, used for nested fields (null for top-level fields)
     * @param parentAddedInChangeSet a flag indicating whether the parent field was added in the same changeset, which would exclude nested fields from being collected
     * @param alterColumnDefinitions the list to collect the resulting column definitions in the format "qualifiedColumnName typeDefinition"
     */
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

            String qualifiedName = parentPath == null
                    ? field.getName()
                    : parentPath + "." + field.getName();

            boolean addedInThisChangeSet = false;
            if (!parentAddedInChangeSet && field.getChangeSetNumber() == changeSetNumber) {
                String typeDefinition = BigQueryTypeDefinitionBuilder.getTypeDefinition(field);
                if (typeDefinition != null) {
                    alterColumnDefinitions.add(qualifiedName + " " + typeDefinition);
                    addedInThisChangeSet = true;
                }
            }

            collectAlterColumnsRecursively(
                    field.getNestedFields(), changeSetNumber, qualifiedName,
                    parentAddedInChangeSet || addedInThisChangeSet, alterColumnDefinitions);
        }
    }

    /**
     * Extracts only the column name from a full column definition string such as
     * "address STRUCT<city STRING>" → "address".
     *
     * @param columnDefinition the full column definition string in the format "qualifiedColumnName typeDefinition"
     * @return the top-level column name extracted from the column definition, without the type definition
     */
    static String extractTopLevelColumnName(String columnDefinition) {
        int firstSpaceIndex = columnDefinition.indexOf(' ');
        return firstSpaceIndex < 0 ? columnDefinition : columnDefinition.substring(0, firstSpaceIndex);
    }
}


