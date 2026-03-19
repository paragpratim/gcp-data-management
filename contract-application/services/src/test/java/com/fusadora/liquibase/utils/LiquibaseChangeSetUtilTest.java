package com.fusadora.liquibase.utils;

import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalTable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LiquibaseChangeSetUtilTest {

    private PhysicalTable createSamplePhysicalTable() {
        PhysicalField field1 = new PhysicalField();
        field1.setName("id");
        field1.setType("INT64");
        field1.setChangeSetNumber(1);

        PhysicalField field2 = new PhysicalField();
        field2.setName("name");
        field2.setType("STRUCT");
        field2.setChangeSetNumber(1);

        PhysicalField field2Nested = new PhysicalField();
        field2Nested.setName("initial");
        field2Nested.setType("STRING");
        field2Nested.setChangeSetNumber(1);
        field2.setNestedFields(List.of(field2Nested));

        PhysicalField field3 = new PhysicalField();
        field3.setName("address");
        field3.setType("STRUCT");
        field3.setChangeSetNumber(2);

        PhysicalField field3Nested = new PhysicalField();
        field3Nested.setName("line1");
        field3Nested.setType("STRING");
        field3Nested.setChangeSetNumber(2);
        field3.setNestedFields(List.of(field3Nested));

        PhysicalField field4 = new PhysicalField();
        field4.setName("city");
        field4.setType("STRING");
        field4.setChangeSetNumber(2);

        PhysicalField field5 = new PhysicalField();
        field5.setName("country");
        field5.setType("STRUCT");
        field5.setChangeSetNumber(3);

        PhysicalField field5Nested = new PhysicalField();
        field5Nested.setName("code");
        field5Nested.setType("STRING");
        field5Nested.setChangeSetNumber(3);
        field5.setNestedFields(List.of(field5Nested));

        PhysicalTable table = new PhysicalTable();
        table.setName("test_table");
        table.setPhysicalFields(Arrays.asList(field1, field2, field3, field4, field5));
        table.setCurrentChangeSetNumber(3);

        return table;
    }

    private PhysicalTable createDeepNestedPhysicalTable() {
        PhysicalField id = new PhysicalField();
        id.setName("id");
        id.setType("INT64");
        id.setChangeSetNumber(1);

        PhysicalField profile = new PhysicalField();
        profile.setName("profile");
        profile.setType("STRUCT");
        profile.setChangeSetNumber(1);

        PhysicalField personal = new PhysicalField();
        personal.setName("personal");
        personal.setType("STRUCT");
        personal.setChangeSetNumber(1);

        PhysicalField firstName = new PhysicalField();
        firstName.setName("first_name");
        firstName.setType("STRING");
        firstName.setChangeSetNumber(1);

        personal.setNestedFields(List.of(firstName));
        profile.setNestedFields(List.of(personal));

        PhysicalField preferences = new PhysicalField();
        preferences.setName("preferences");
        preferences.setType("STRUCT");
        preferences.setChangeSetNumber(2);

        PhysicalField notifications = new PhysicalField();
        notifications.setName("notifications");
        notifications.setType("STRUCT");
        notifications.setChangeSetNumber(2);

        PhysicalField email = new PhysicalField();
        email.setName("email");
        email.setType("BOOL");
        email.setChangeSetNumber(2);

        notifications.setNestedFields(List.of(email));
        preferences.setNestedFields(List.of(notifications));

        PhysicalTable table = new PhysicalTable();
        table.setName("deep_nested_table");
        table.setPhysicalFields(Arrays.asList(id, profile, preferences));
        table.setCurrentChangeSetNumber(2);

        return table;
    }

    private PhysicalTable createMultiFieldStructPhysicalTable() {
        PhysicalField id = new PhysicalField();
        id.setName("id");
        id.setType("INT64");
        id.setChangeSetNumber(1);

        PhysicalField person = new PhysicalField();
        person.setName("person");
        person.setType("STRUCT");
        person.setChangeSetNumber(1);

        PhysicalField firstName = new PhysicalField();
        firstName.setName("first_name");
        firstName.setType("STRING");
        firstName.setChangeSetNumber(1);

        PhysicalField lastName = new PhysicalField();
        lastName.setName("last_name");
        lastName.setType("STRING");
        lastName.setChangeSetNumber(1);

        person.setNestedFields(List.of(firstName, lastName));

        PhysicalField address = new PhysicalField();
        address.setName("address");
        address.setType("STRUCT");
        address.setChangeSetNumber(2);

        PhysicalField line1 = new PhysicalField();
        line1.setName("line1");
        line1.setType("STRING");
        line1.setChangeSetNumber(2);

        PhysicalField line2 = new PhysicalField();
        line2.setName("line2");
        line2.setType("STRING");
        line2.setChangeSetNumber(2);

        address.setNestedFields(List.of(line1, line2));

        PhysicalTable table = new PhysicalTable();
        table.setName("multi_field_struct_table");
        table.setPhysicalFields(Arrays.asList(id, person, address));
        table.setCurrentChangeSetNumber(2);

        return table;
    }

    @Test
    void testGetLiquibaseChangeSetSql_CreateTable() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createSamplePhysicalTable(), "dataset");

        assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS dataset.test_table"));
        assertTrue(sql.contains("id INT64"));
        assertTrue(sql.contains("name STRUCT<initial STRING>"));
        assertTrue(sql.contains("--rollback DROP TABLE IF EXISTS dataset.test_table;"));

        assertTrue(sql.contains("ALTER TABLE dataset.test_table"));
        assertTrue(sql.contains("    ADD COLUMN IF NOT EXISTS address STRUCT<line1 STRING>"));
        assertTrue(sql.contains("    ADD COLUMN IF NOT EXISTS country STRUCT<code STRING>"));
        assertTrue(sql.contains("    ADD COLUMN IF NOT EXISTS city STRING"));
        assertTrue(sql.contains(";"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.test_table DROP COLUMN IF EXISTS address;"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.test_table DROP COLUMN IF EXISTS country;"));
    }

    @Test
    void testGetLiquibaseChangeSetSql_DeepNestedCreateAndAlterStatements() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createDeepNestedPhysicalTable(), "dataset");

        assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS dataset.deep_nested_table"));
        assertTrue(sql.contains("profile STRUCT<personal STRUCT<first_name STRING>>"));

        assertTrue(sql.contains("ALTER TABLE dataset.deep_nested_table"));
        assertTrue(sql.contains("ADD COLUMN IF NOT EXISTS preferences STRUCT<notifications STRUCT<email BOOL>>"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.deep_nested_table DROP COLUMN IF EXISTS preferences;"));
    }

    @Test
    void testGetLiquibaseChangeSetSql_StructWithMultipleFields() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createMultiFieldStructPhysicalTable(), "dataset");

        assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS dataset.multi_field_struct_table"));
        assertTrue(sql.contains("person STRUCT<first_name STRING, last_name STRING>"));

        assertTrue(sql.contains("ALTER TABLE dataset.multi_field_struct_table"));
        assertTrue(sql.contains("ADD COLUMN IF NOT EXISTS address STRUCT<line1 STRING, line2 STRING>"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.multi_field_struct_table DROP COLUMN IF EXISTS address;"));
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
        assertTrue(content.contains("name STRUCT<initial STRING>"));
        assertTrue(content.contains("address STRUCT<line1 STRING>"));

        // Cleanup
        Files.deleteIfExists(expectedFile);
        Files.deleteIfExists(tempDir.resolve(projectId).resolve(dataProductVersion).resolve(dataSetName));
        Files.deleteIfExists(tempDir.resolve(projectId).resolve(dataProductVersion));
        Files.deleteIfExists(tempDir.resolve(projectId));
        Files.deleteIfExists(tempDir);
    }
}
