package org.fusadora.liquibase.utils;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;

public class LiquibaseCommandUtil {

    private LiquibaseCommandUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void updateBigQuery(String changelogFile, String projectId, String dataset, String credentialsPath) throws Exception {
        String url = "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=" + projectId + ";DefaultDataset=" + dataset + ";OAuthType=3;";
        try (Connection conn = DriverManager.getConnection(url)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new liquibase.database.jvm.JdbcConnection(conn));
            ResourceAccessor resourceAccessor = new DirectoryResourceAccessor(new java.io.File(".").toPath());
            Liquibase liquibase = new Liquibase(changelogFile, resourceAccessor, database);
            liquibase.update((String) null);
        }
    }

}
