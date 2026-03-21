package com.fusadora.liquibase.utils.changeset;

import com.fusadora.model.datacontract.PhysicalField;

import java.util.ArrayList;
import java.util.List;

/**
 * com.fusadora.liquibase.utils.changeset.BigQueryTypeDefinitionBuilder
 * Builds BigQuery-compatible SQL type definitions for STRUCT and ARRAY fields,
 * including recursive nested field resolution.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
class BigQueryTypeDefinitionBuilder {

    private BigQueryTypeDefinitionBuilder() {
    }

    /**
     * Returns the BigQuery SQL type string for a field, resolving STRUCT and ARRAY
     * types recursively. Returns null if the field or its type is null.
     *
     * @param field the PhysicalField for which to build the type definition
     * @return the BigQuery SQL type definition string, or null if the field/type is null
     */
    static String getTypeDefinition(PhysicalField field) {
        if (field == null || field.getType() == null) {
            return null;
        }

        if ("ARRAY".equalsIgnoreCase(field.getType())) {
            return buildArrayDefinition(field);
        }

        List<String> nestedDefinitions = buildNestedDefinitions(field.getNestedFields());
        if (!nestedDefinitions.isEmpty()) {
            return "STRUCT<" + String.join(", ", nestedDefinitions) + ">";
        }
        return field.getType();
    }

    /**
     * Builds the BigQuery SQL type definition for an ARRAY field, handling both
     * primitive arrays (with a single unnamed element field) and arrays of STRUCTs.
     *
     * @param field the PhysicalField representing the ARRAY type
     * @return the BigQuery SQL type definition string for the ARRAY, or the original type if it cannot be resolved
     */
    private static String buildArrayDefinition(PhysicalField field) {
        List<PhysicalField> nestedFields = field.getNestedFields();
        if (nestedFields == null || nestedFields.isEmpty()) {
            return field.getType();
        }

        // Support a primitive array via a single unnamed element field.
        if (nestedFields.size() == 1) {
            PhysicalField elementField = nestedFields.get(0);
            if (elementField != null
                    && (elementField.getName() == null || elementField.getName().isBlank())
                    && elementField.getType() != null) {
                String elementType = getTypeDefinition(elementField);
                if (elementType != null) {
                    return "ARRAY<" + elementType + ">";
                }
            }
        }

        List<String> nestedDefinitions = buildNestedDefinitions(nestedFields);
        if (!nestedDefinitions.isEmpty()) {
            return "ARRAY<STRUCT<" + String.join(", ", nestedDefinitions) + ">>";
        }
        return field.getType();
    }

    /**
     * Builds the list of inline field definitions for a STRUCT or ARRAY body,
     * including description OPTIONS for each nested field.
     *
     * @param fields the list of PhysicalField objects representing the nested fields
     * @return a list of strings, each representing a field definition in the format "name type [OPTIONS]", suitable for inclusion in a STRUCT or ARRAY definition
     */
    static List<String> buildNestedDefinitions(List<PhysicalField> fields) {
        List<String> nestedDefinitions = new ArrayList<>();
        if (fields == null || fields.isEmpty()) {
            return nestedDefinitions;
        }

        for (PhysicalField field : fields) {
            if (field == null || field.getName() == null) {
                continue;
            }

            String typeDefinition = getTypeDefinition(field);
            if (typeDefinition != null) {
                nestedDefinitions.add(field.getName() + " " + typeDefinition
                        + LiquibaseChangeSetConstants.getInlineDescriptionOptions(field.getDescription()));
            }
        }
        return nestedDefinitions;
    }
}


