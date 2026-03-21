package com.fusadora.liquibase.utils.changeset;

/**
 * com.fusadora.liquibase.utils.changeset.LiquibaseChangeSetConstants
 * This class defines constants and utility methods for generating Liquibase change sets.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
class LiquibaseChangeSetConstants {

    static final String CHANGESET_AUTHOR = "fusadora";
    static final String EMPTY_DESCRIPTION = "''";
    static final String ALTER_TABLE_PREFIX = "ALTER TABLE ";
    static final String ROLLBACK_ALTER_TABLE_PREFIX = "--rollback ALTER TABLE ";
    static final String SET_OPTIONS_DESCRIPTION_PREFIX = " SET OPTIONS(description=";

    private LiquibaseChangeSetConstants() {
    }

    /**
     * Escapes single quotes in the description and wraps it in single quotes for use in Liquibase change sets.
     *
     * @param description The description to be escaped and formatted. If null or blank, it returns an empty description literal.
     * @return A string that is properly escaped and formatted for use as a description in Liquibase change sets.
     */
    static String getEscapedDescriptionLiteral(String description) {
        if (description == null || description.isBlank()) {
            return EMPTY_DESCRIPTION;
        }
        return "'" + description.replace("'", "''") + "'";
    }

    /**
     * Generates the OPTIONS clause for a Liquibase change set based on the provided description. If the description is null or blank, it returns an empty string.
     *
     * @param description The description to be included in the OPTIONS clause. If null or blank, it will not include the OPTIONS clause.
     * @return A string that contains the OPTIONS clause with the escaped description, or an empty string if the description is null or blank.
     */
    static String getInlineDescriptionOptions(String description) {
        if (description == null || description.isBlank()) {
            return "";
        }
        return " OPTIONS(description=" + getEscapedDescriptionLiteral(description) + ")";
    }
}


