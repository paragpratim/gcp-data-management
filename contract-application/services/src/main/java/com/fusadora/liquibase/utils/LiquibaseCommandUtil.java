package com.fusadora.liquibase.utils;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LiquibaseCommandUtil {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LiquibaseCommandUtil.class);

    private LiquibaseCommandUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void updateBigQuery(String aProjectId, String aDataset, String liquibasePath, String dataProductVersion) {
        String url = "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=" + aProjectId + ";DefaultDataset=" + aDataset + ";OAuthType=3;";

        // Establishing connection to BigQuery
        Database database = getConnection(url);
        if (database == null) {
            logger.error("No further processing possible. Database connection could not be established");
            return;
        }

        // Changelog file path
        // File Path: <liquibasePath>/<projectId>/<version>/<dataSetName>.json
        Path changelogFilePath = Path.of(liquibasePath).resolve(aProjectId).resolve(dataProductVersion);
        String changelogFile = changelogFilePath.resolve(aDataset + ".json").toString();

        // ResourceAccessor pointing to the directory containing changelog and SQL files for the specific project
        ResourceAccessor resourceAccessor = getResourceAccessor(changelogFilePath);
        if (resourceAccessor == null) {
            logger.error("No further processing possible. ResourceAccessor could not be created");
            return;
        }

        try (Liquibase liquibase = new Liquibase(changelogFile, resourceAccessor, database);) {
            liquibase.update((String) null);
            // Tagging the database with the data product version and current timestamp
            liquibase.tag(dataProductVersion + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        } catch (LiquibaseException e) {
            logger.error("Error Connecting to BigQuery Project:[{}];Dataset:[{}]", aProjectId, aDataset, e);
        }
    }

    private static Database getConnection(String url) {
        try (Connection conn = DriverManager.getConnection(url)) {
            return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new liquibase.database.jvm.JdbcConnection(conn));
        } catch (SQLException | DatabaseException e) {
            logger.error("Unable to connect to the database with URL: {}", url, e);
            return null;
        }
    }

    private static ResourceAccessor getResourceAccessor(Path changelogFilePath) {
        try (DirectoryResourceAccessor resourceAccessor = new DirectoryResourceAccessor(changelogFilePath)) {
            return resourceAccessor;
        } catch (Exception e) {
            logger.error("Error creating ResourceAccessor for path: {}", changelogFilePath, e);
            return null;
        }
    }

}
