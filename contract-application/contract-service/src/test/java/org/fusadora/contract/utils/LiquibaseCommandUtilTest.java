package org.fusadora.contract.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LiquibaseCommandUtilTest {

    @Test
    void testUpdateBigQuery() {
        // Arrange: set up any required parameters or mocks
        String changelogFile = "test-changelog.xml";
        String jdbcUrl = "jdbc:bigquery://https://googleapis.com/bigquery/v2:443;ProjectId=gcp-data-plane-458721;OAuthType=3;DefaultDataset=liquibase_changelog;";
        String username = "test-user";
        String password = "test-pass";

        // Act & Assert: call the method and check for exceptions or expected results
        assertDoesNotThrow(() ->
                LiquibaseCommandUtil.updateBigQuery(changelogFile, jdbcUrl, username, password)
        );
    }
}