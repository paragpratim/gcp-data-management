package com.fusadora.liquibase.utils;

import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalTable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LiquibaseChangeSetUtilTest {

    private static int countOccurrences(String text, String token) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(token, index)) >= 0) {
            count++;
            index += token.length();
        }
        return count;
    }

    private PhysicalTable createSamplePhysicalTable() {
        PhysicalField field1 = new PhysicalField();
        field1.setName("id");
        field1.setType("INT64");
        field1.setChangeSetNumber(1);
        field1.setDescription("Primary key");

        PhysicalField field2 = new PhysicalField();
        field2.setName("name");
        field2.setType("STRUCT");
        field2.setChangeSetNumber(1);

        PhysicalField field2Nested = new PhysicalField();
        field2Nested.setName("initial");
        field2Nested.setType("STRING");
        field2Nested.setChangeSetNumber(1);
        field2Nested.setDescription("Initial character");
        field2.setNestedFields(List.of(field2Nested));
        field2.setDescription("Name record");

        PhysicalField field3 = new PhysicalField();
        field3.setName("address");
        field3.setType("STRUCT");
        field3.setChangeSetNumber(2);

        PhysicalField field3Nested = new PhysicalField();
        field3Nested.setName("line1");
        field3Nested.setType("STRING");
        field3Nested.setChangeSetNumber(2);
        field3Nested.setDescription("Address line 1");
        field3.setNestedFields(List.of(field3Nested));
        field3.setDescription("Address struct");

        PhysicalField field4 = new PhysicalField();
        field4.setName("city");
        field4.setType("STRING");
        field4.setChangeSetNumber(2);
        field4.setDescription("City name");

        PhysicalField field5 = new PhysicalField();
        field5.setName("country");
        field5.setType("STRUCT");
        field5.setChangeSetNumber(3);

        PhysicalField field5Nested = new PhysicalField();
        field5Nested.setName("code");
        field5Nested.setType("STRING");
        field5Nested.setChangeSetNumber(3);
        field5Nested.setDescription("Country code");
        field5.setNestedFields(List.of(field5Nested));
        field5.setDescription("Country struct");

        PhysicalTable table = new PhysicalTable();
        table.setName("test_table");
        table.setDescription("Test table description");
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

    private PhysicalTable createStructWithMixedNestedChangeSetTable() {
        PhysicalField id = new PhysicalField();
        id.setName("id");
        id.setType("INT64");
        id.setChangeSetNumber(1);

        PhysicalField orderLine = new PhysicalField();
        orderLine.setName("order_line");
        orderLine.setType("STRUCT");
        orderLine.setChangeSetNumber(3);
        orderLine.setDescription("Order line details");

        PhysicalField sku = new PhysicalField();
        sku.setName("sku");
        sku.setType("STRING");
        sku.setChangeSetNumber(1);
        sku.setDescription("Stock keeping unit");

        PhysicalField quantity = new PhysicalField();
        quantity.setName("quantity");
        quantity.setType("INT64");
        quantity.setChangeSetNumber(1);
        quantity.setDescription("Quantity ordered");

        PhysicalField pricing = new PhysicalField();
        pricing.setName("pricing");
        pricing.setType("STRUCT");
        pricing.setChangeSetNumber(1);
        pricing.setDescription("Pricing details");

        PhysicalField amount = new PhysicalField();
        amount.setName("amount");
        amount.setType("NUMERIC");
        amount.setChangeSetNumber(1);
        amount.setDescription("Line amount");

        PhysicalField currency = new PhysicalField();
        currency.setName("currency");
        currency.setType("STRING");
        currency.setChangeSetNumber(1);
        currency.setDescription("Currency code");

        pricing.setNestedFields(List.of(amount, currency));
        orderLine.setNestedFields(List.of(sku, quantity, pricing));

        PhysicalTable table = new PhysicalTable();
        table.setName("sales_order_1");
        table.setDescription("Sales order table");
        table.setPhysicalFields(Arrays.asList(id, orderLine));
        table.setCurrentChangeSetNumber(3);

        return table;
    }

    @Test
    void testGetLiquibaseChangeSetSql_CreateTable() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createSamplePhysicalTable(), "dataset");

        // CREATE TABLE must have no description OPTIONS -- stays permanently immutable
        assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS dataset.test_table"));
        assertTrue(sql.contains("id INT64 OPTIONS(description='Primary key')"));
        assertTrue(sql.contains("name STRUCT<initial STRING OPTIONS(description='Initial character')> OPTIONS(description='Name record')"));
        assertFalse(sql.contains(") OPTIONS(description='Test table description');"));
        assertTrue(sql.contains(");"));
        assertTrue(sql.contains("--rollback DROP TABLE IF EXISTS dataset.test_table;"));

        // Table description must be in a dedicated _1_desc changeset
        assertTrue(sql.contains("--changeset fusadora:test_table_1_desc"));
        assertTrue(sql.contains("ALTER TABLE dataset.test_table SET OPTIONS(description='Test table description');"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.test_table SET OPTIONS(description='');"));

        // Alter changesets for new columns
        assertTrue(sql.contains("ALTER TABLE dataset.test_table"));
        assertTrue(sql.contains("    ADD COLUMN IF NOT EXISTS address STRUCT<line1 STRING OPTIONS(description='Address line 1')> OPTIONS(description='Address struct')"));
        assertTrue(sql.contains("    ADD COLUMN IF NOT EXISTS country STRUCT<code STRING OPTIONS(description='Country code')> OPTIONS(description='Country struct')"));
        assertTrue(sql.contains("    ADD COLUMN IF NOT EXISTS city STRING OPTIONS(description='City name')"));
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
    void testGetLiquibaseChangeSetSql_AlterStructIncludesNestedFieldsWithMixedChangeSets() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createStructWithMixedNestedChangeSetTable(), "raw_data");

        assertTrue(sql.contains("--changeset fusadora:sales_order_1_3"));
        assertTrue(sql.contains("ALTER TABLE raw_data.sales_order_1"));
        assertTrue(sql.contains("ADD COLUMN IF NOT EXISTS order_line STRUCT<sku STRING OPTIONS(description='Stock keeping unit'), quantity INT64 OPTIONS(description='Quantity ordered'), pricing STRUCT<amount NUMERIC OPTIONS(description='Line amount'), currency STRING OPTIONS(description='Currency code')> OPTIONS(description='Pricing details')> OPTIONS(description='Order line details')"));
        // Table description is in its own _1_desc changeset, not mixed into alter changesets
        assertTrue(sql.contains("--changeset fusadora:sales_order_1_1_desc"));
        assertTrue(sql.contains("ALTER TABLE raw_data.sales_order_1 SET OPTIONS(description='Sales order table');"));
        assertTrue(sql.contains("--rollback ALTER TABLE raw_data.sales_order_1 DROP COLUMN IF EXISTS order_line;"));
    }

    @Test
    void testGetLiquibaseChangeSetSql_DescriptionEscaping() {
        PhysicalField id = new PhysicalField();
        id.setName("id");
        id.setType("INT64");
        id.setChangeSetNumber(1);
        id.setDescription("Order's identifier");

        PhysicalTable table = new PhysicalTable();
        table.setName("orders");
        table.setDescription("Customer's orders");
        table.setPhysicalFields(List.of(id));
        table.setCurrentChangeSetNumber(1);

        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(table, "dataset");

        assertTrue(sql.contains("id INT64 OPTIONS(description='Order''s identifier')"));
        // Table description must be in the dedicated _1_desc changeset, not in CREATE TABLE body
        assertFalse(sql.contains(") OPTIONS(description='Customer''s orders');"));
        assertTrue(sql.contains("--changeset fusadora:orders_1_desc"));
        assertTrue(sql.contains("ALTER TABLE dataset.orders SET OPTIONS(description='Customer''s orders');"));
    }

    @Test
    void testGetLiquibaseChangeSetSql_OmitsDescriptionOptionsWhenNullOrBlank() {
        PhysicalField id = new PhysicalField();
        id.setName("id");
        id.setType("INT64");
        id.setChangeSetNumber(1);
        id.setDescription(null);

        PhysicalField city = new PhysicalField();
        city.setName("city");
        city.setType("STRING");
        city.setChangeSetNumber(2);
        city.setDescription("   ");

        PhysicalTable table = new PhysicalTable();
        table.setName("description_omission_table");
        table.setDescription(" ");
        table.setPhysicalFields(List.of(id, city));
        table.setCurrentChangeSetNumber(2);

        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(table, "dataset");

        assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS dataset.description_omission_table"));
        assertTrue(sql.contains("id INT64"));
        assertFalse(sql.contains("id INT64 OPTIONS(description="));

        assertTrue(sql.contains("ALTER TABLE dataset.description_omission_table"));
        assertTrue(sql.contains("ADD COLUMN IF NOT EXISTS city STRING"));
        assertFalse(sql.contains("ADD COLUMN IF NOT EXISTS city STRING OPTIONS(description="));

        assertFalse(sql.contains("SET OPTIONS(description="));
    }

    @Test
    void testGetLiquibaseChangeSetSql_TableDescriptionIsOnlyInDedicatedDescChangeset() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createSamplePhysicalTable(), "dataset");

        // Description must NOT appear inside CREATE TABLE body
        assertFalse(sql.contains(") OPTIONS(description='Test table description');"));

        // Description must appear exactly once in the dedicated _1_desc changeset
        assertTrue(sql.contains("--changeset fusadora:test_table_1_desc"));
        assertEquals(1, countOccurrences(sql, "ALTER TABLE dataset.test_table SET OPTIONS(description='Test table description');"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.test_table SET OPTIONS(description='');"));
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
        assertTrue(content.contains("id INT64 OPTIONS(description='Primary key')"));
        assertTrue(content.contains("name STRUCT<initial STRING OPTIONS(description='Initial character')> OPTIONS(description='Name record')"));
        assertTrue(content.contains("address STRUCT<line1 STRING OPTIONS(description='Address line 1')> OPTIONS(description='Address struct')"));

        // Cleanup
        Files.deleteIfExists(expectedFile);
        Files.deleteIfExists(tempDir.resolve(projectId).resolve(dataProductVersion).resolve(dataSetName));
        Files.deleteIfExists(tempDir.resolve(projectId).resolve(dataProductVersion));
        Files.deleteIfExists(tempDir.resolve(projectId));
        Files.deleteIfExists(tempDir);
    }
}
