package org.fusadora.liquibase.utils;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

public class LiquibaseCommandUtil {

    private LiquibaseCommandUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void updateBigQuery(String aProjectId, String aDataset, String liquibasePath) throws Exception {
        String url = "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=" + aProjectId + ";DefaultDataset=" + aDataset + ";OAuthType=3;";
        try (Connection conn = DriverManager.getConnection(url)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new liquibase.database.jvm.JdbcConnection(conn));
            // ResourceAccessor pointing to the directory containing changelog and SQL files for the specific project
            Path changelogFilePath = Path.of(liquibasePath).resolve(aProjectId);
            ResourceAccessor resourceAccessor = new DirectoryResourceAccessor(changelogFilePath);
            // Changelog file path
            String changelogFile = changelogFilePath.resolve(aDataset + ".json").toString();
            try (Liquibase liquibase = new Liquibase(changelogFile, resourceAccessor, database)) {
                liquibase.update((String) null);
            } catch (LiquibaseException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
