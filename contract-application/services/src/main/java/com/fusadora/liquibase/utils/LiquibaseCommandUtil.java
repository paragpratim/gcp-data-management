package com.fusadora.liquibase.utils;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * com.fusadora.liquibase.utils.LiquibaseCommandUtil
 * Utility class for executing Liquibase commands on BigQuery databases.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

public class LiquibaseCommandUtil {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LiquibaseCommandUtil.class);

    private LiquibaseCommandUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Updates the BigQuery database using Liquibase changelogs.
     *
     * @param aProjectId        The GCP project ID.
     * @param aDataset          The BigQuery dataset name.
     * @param liquibasePath     The base path where Liquibase changelogs are stored.
     * @param dataProductVersion The version of the data product.
     * @param contractId        The contract identifier.
     * @param defaultDataset    The default dataset for the connection.
     * @return A message indicating success or failure of the update operation.
     */
    public static String updateBigQuery(String aProjectId, String aDataset, String liquibasePath, String dataProductVersion, String contractId, String defaultDataset) {
        String url = "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=" + aProjectId + ";DefaultDataset=" + defaultDataset + ";OAuthType=3;";

        // Changelog file path
        // File Path: <liquibasePath>/<projectId>/<version>/<dataSetName>.json
        Path changelogFilePath = Path.of(liquibasePath).resolve(aProjectId).resolve(dataProductVersion);
        String changelogFile = aDataset + ".json"; // filename relative to the DirectoryResourceAccessor
        Path fullChangelogPath = changelogFilePath.resolve(changelogFile);

        if (!Files.exists(fullChangelogPath)) {
            logger.error("Changelog file does not exist: {}", fullChangelogPath);
            return "Failed: changelog not found at " + fullChangelogPath;
        }

        try (Connection conn = DriverManager.getConnection(url);
             liquibase.database.jvm.JdbcConnection jdbc = new liquibase.database.jvm.JdbcConnection(conn);
             ResourceAccessor resourceAccessor = new DirectoryResourceAccessor(changelogFilePath);
             Liquibase liquibase = new Liquibase(
                     changelogFile,
                     resourceAccessor,
                     DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbc))) {

            liquibase.update((String) null);
            liquibase.tag(
                    contractId + "_" + dataProductVersion + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"))
            );

        } catch (Exception e) {
            logger.error("Error Connecting to BigQuery Project:[{}]:Dataset:[{}] for Contract [{}]", aProjectId, aDataset, contractId, e);
            return "Failed to update BigQuery for Contract [" + contractId + "]";
        }
        return "Successfully updated BigQuery for Contract [" + contractId + "]";
    }
}