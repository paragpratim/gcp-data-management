package com.fusadora.liquibase.utils;

import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalTable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LiquibaseChangeSetUtilTest {

    private PhysicalTable createSamplePhysicalTable() {
        PhysicalField field1 = new PhysicalField();
        field1.setName("id");
        field1.setType("INT64");
        field1.setChangeSetNumber(1);

        PhysicalField field2 = new PhysicalField();
        field2.setName("name");
        field2.setType("STRING");
        field2.setChangeSetNumber(1);

        PhysicalField field3 = new PhysicalField();
        field3.setName("address");
        field3.setType("STRING");
        field3.setChangeSetNumber(2);

        PhysicalField field4 = new PhysicalField();
        field4.setName("city");
        field4.setType("STRING");
        field4.setChangeSetNumber(2);

        PhysicalField field5 = new PhysicalField();
        field5.setName("country");
        field5.setType("STRING");
        field5.setChangeSetNumber(3);

        PhysicalTable table = new PhysicalTable();
        table.setName("test_table");
        table.setPhysicalFields(Arrays.asList(field1, field2, field3, field4, field5));
        table.setCurrentChangeSetNumber(3);

        return table;
    }

    @Test
    void testGetLiquibaseChangeSetSql_CreateTable() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createSamplePhysicalTable(), "dataset");

        System.out.println(sql); // For debugging purposes

        assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS dataset.test_table"));
        assertTrue(sql.contains("id INT64"));
        assertTrue(sql.contains("name STRING"));
        assertTrue(sql.contains("--rollback DROP TABLE IF EXISTS dataset.test_table;"));

        assertTrue(sql.contains("ALTER TABLE dataset.test_table"));
        assertTrue(sql.contains("    ADD COLUMN IF NOT EXISTS address STRING"));
        assertTrue(sql.contains(";"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.test_table DROP COLUMN IF EXISTS address;"));
    }

    @Test
    void testGenerateLiquibaseChangeSetSqlFile() throws IOException {
        // Arrange
        PhysicalTable table = createSamplePhysicalTable();
        String dataSetName = "test_dataset";
        String projectId = "test_project";
        String dataProductVersion = "v1";
        Path tempDir = Path.of("").toAbsolutePath().resolve("liquibase_test");
        Files.createDirectories(tempDir);
        String filePath = tempDir.toString();

        // Act
        LiquibaseChangeSetUtil.generateLiquibaseChangeSetSqlFile(table, projectId, dataSetName, filePath, dataProductVersion);

        // Assert
        Path expectedDir = tempDir.resolve(projectId).resolve(dataProductVersion).resolve(dataSetName);
        Path expectedFile = expectedDir.resolve("test_table.sql");
        assertTrue(Files.exists(expectedFile), "SQL file should be created");

        String content = Files.readString(expectedFile);
        assertTrue(content.contains("CREATE TABLE IF NOT EXISTS test_dataset.test_table"));
        assertTrue(content.contains("id INT64"));
        assertTrue(content.contains("name STRING"));

        // Cleanup
        Files.deleteIfExists(expectedFile);
        Files.deleteIfExists(tempDir.resolve(projectId).resolve(dataProductVersion).resolve(dataSetName));
        Files.deleteIfExists(tempDir.resolve(projectId).resolve(dataProductVersion));
        Files.deleteIfExists(tempDir.resolve(projectId));
        Files.deleteIfExists(tempDir);
    }
}
