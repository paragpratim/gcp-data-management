package org.fusadora.liquibase.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LiquibaseChangeLogUtilTest {

    @Test
    void getLiquibaseChangeLogJson() throws JsonProcessingException {
        String dataSetName = "my_dataset";
        String expectedJson = """
                {
                  "databaseChangeLog" : [ {
                    "includeAll" : {
                      "path" : "my_dataset/",
                      "relativeToChangelogFile" : true
                    }
                  } ]
                }""";

        String actualJson = LiquibaseChangeLogUtil.getLiquibaseChangeLogJson(dataSetName);

        assertEquals(expectedJson, actualJson);
    }

    @Test
    void testGenerateLiquibaseChangeLogJsonFile() throws IOException {
        String dataSetName = "test_dataset";
        String projectId = "test_project";
        String dataProductVersion = "v1";
        Path tempDir = Path.of("").toAbsolutePath().resolve("liquibase_changelog_test");
        Files.createDirectories(tempDir);
        String filePath = tempDir.toString();

        // Act
        LiquibaseChangeLogUtil.generateLiquibaseChangeLogJsonFile(projectId, dataSetName, filePath, dataProductVersion);

        // Assert
        Path expectedDir = tempDir.resolve(projectId).resolve(dataProductVersion);
        Path expectedFile = expectedDir.resolve(dataSetName + ".json");
        assertTrue(Files.exists(expectedFile), "JSON file should be created");

        String content = Files.readString(expectedFile);
        assertTrue(content.contains("\"path\" : \"test_dataset/\""));
        assertTrue(content.contains("\"relativeToChangelogFile\" : true"));

        // Cleanup
        Files.deleteIfExists(expectedFile);
        Files.deleteIfExists(tempDir.resolve(projectId).resolve(dataProductVersion));
        Files.deleteIfExists(tempDir.resolve(projectId));
        Files.deleteIfExists(tempDir);
    }
}