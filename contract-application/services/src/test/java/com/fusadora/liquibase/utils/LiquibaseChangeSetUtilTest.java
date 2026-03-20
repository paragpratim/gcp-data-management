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

    private PhysicalTable createArrayPhysicalTable() {
        PhysicalField id = new PhysicalField();
        id.setName("id");
        id.setType("INT64");
        id.setChangeSetNumber(1);

        PhysicalField lineItems = new PhysicalField();
        lineItems.setName("line_items");
        lineItems.setType("ARRAY");
        lineItems.setChangeSetNumber(1);

        PhysicalField sku = new PhysicalField();
        sku.setName("sku");
        sku.setType("STRING");
        sku.setChangeSetNumber(1);

        PhysicalField qty = new PhysicalField();
        qty.setName("qty");
        qty.setType("INT64");
        qty.setChangeSetNumber(1);

        lineItems.setNestedFields(List.of(sku, qty));

        PhysicalField tags = new PhysicalField();
        tags.setName("tags");
        tags.setType("ARRAY");
        tags.setChangeSetNumber(2);

        // Unnamed single nested field represents array element type.
        PhysicalField tagElement = new PhysicalField();
        tagElement.setType("STRING");
        tagElement.setChangeSetNumber(2);
        tags.setNestedFields(List.of(tagElement));

        PhysicalTable table = new PhysicalTable();
        table.setName("array_table");
        table.setPhysicalFields(List.of(id, lineItems, tags));
        table.setCurrentChangeSetNumber(2);
        return table;
    }

    @Test
    void testGetLiquibaseChangeSetSql_CreateTable() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createSamplePhysicalTable(), "dataset");

        // Structural changesets must stay description-free and immutable
        assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS dataset.test_table"));
        assertTrue(sql.contains("id INT64"));
        assertTrue(sql.contains("name STRUCT<initial STRING>"));
        assertFalse(sql.contains("id INT64 OPTIONS(description='Primary key')"));
        assertFalse(sql.contains("name STRUCT<initial STRING OPTIONS(description='Initial character')> OPTIONS(description='Name record')"));
        assertTrue(sql.contains(");"));
        assertTrue(sql.contains("--rollback DROP TABLE IF EXISTS dataset.test_table;"));

        // Dedicated mutable description changeset carries table and column descriptions
        assertTrue(sql.contains("--changeset fusadora:test_table_desc runOnChange:true"));
        assertTrue(sql.contains("ALTER TABLE dataset.test_table SET OPTIONS(description='Test table description');"));
        assertTrue(sql.contains("ALTER TABLE dataset.test_table ALTER COLUMN id SET OPTIONS(description='Primary key');"));
        assertTrue(sql.contains("ALTER TABLE dataset.test_table ALTER COLUMN name SET OPTIONS(description='Name record');"));
        assertTrue(sql.contains("ALTER TABLE dataset.test_table ALTER COLUMN name.initial SET OPTIONS(description='Initial character');"));
        assertTrue(sql.contains("ALTER TABLE dataset.test_table ALTER COLUMN address SET OPTIONS(description='Address struct');"));
        assertTrue(sql.contains("ALTER TABLE dataset.test_table ALTER COLUMN address.line1 SET OPTIONS(description='Address line 1');"));
        assertTrue(sql.contains("ALTER TABLE dataset.test_table ALTER COLUMN city SET OPTIONS(description='City name');"));
        assertTrue(sql.contains("ALTER TABLE dataset.test_table ALTER COLUMN country SET OPTIONS(description='Country struct');"));
        assertTrue(sql.contains("ALTER TABLE dataset.test_table ALTER COLUMN country.code SET OPTIONS(description='Country code');"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.test_table SET OPTIONS(description='');"));

        // Alter changesets for new columns
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
    void testGetLiquibaseChangeSetSql_ArrayTypes() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createArrayPhysicalTable(), "dataset");

        assertTrue(sql.contains("line_items ARRAY<STRUCT<sku STRING, qty INT64>>"));
        assertTrue(sql.contains("ADD COLUMN IF NOT EXISTS tags ARRAY<STRING>"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.array_table DROP COLUMN IF EXISTS tags;"));
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
        assertTrue(sql.contains("ADD COLUMN IF NOT EXISTS order_line STRUCT<sku STRING, quantity INT64, pricing STRUCT<amount NUMERIC, currency STRING>>"));
        // All descriptions are in one stable mutable _desc changeset
        assertTrue(sql.contains("--changeset fusadora:sales_order_1_desc runOnChange:true"));
        assertTrue(sql.contains("ALTER TABLE raw_data.sales_order_1 SET OPTIONS(description='Sales order table');"));
        assertTrue(sql.contains("ALTER TABLE raw_data.sales_order_1 ALTER COLUMN order_line SET OPTIONS(description='Order line details');"));
        assertTrue(sql.contains("ALTER TABLE raw_data.sales_order_1 ALTER COLUMN order_line.sku SET OPTIONS(description='Stock keeping unit');"));
        assertTrue(sql.contains("ALTER TABLE raw_data.sales_order_1 ALTER COLUMN order_line.quantity SET OPTIONS(description='Quantity ordered');"));
        assertTrue(sql.contains("ALTER TABLE raw_data.sales_order_1 ALTER COLUMN order_line.pricing SET OPTIONS(description='Pricing details');"));
        assertTrue(sql.contains("ALTER TABLE raw_data.sales_order_1 ALTER COLUMN order_line.pricing.amount SET OPTIONS(description='Line amount');"));
        assertTrue(sql.contains("ALTER TABLE raw_data.sales_order_1 ALTER COLUMN order_line.pricing.currency SET OPTIONS(description='Currency code');"));
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

        assertTrue(sql.contains("id INT64"));
        assertFalse(sql.contains("id INT64 OPTIONS(description='Order''s identifier')"));
        assertTrue(sql.contains("--changeset fusadora:orders_desc runOnChange:true"));
        assertTrue(sql.contains("ALTER TABLE dataset.orders SET OPTIONS(description='Customer''s orders');"));
        assertTrue(sql.contains("ALTER TABLE dataset.orders ALTER COLUMN id SET OPTIONS(description='Order''s identifier');"));
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

        assertTrue(sql.contains("--changeset fusadora:description_omission_table_desc runOnChange:true"));
        assertTrue(sql.contains("ALTER TABLE dataset.description_omission_table SET OPTIONS(description='');"));
        assertTrue(sql.contains("ALTER TABLE dataset.description_omission_table ALTER COLUMN id SET OPTIONS(description='');"));
        assertTrue(sql.contains("ALTER TABLE dataset.description_omission_table ALTER COLUMN city SET OPTIONS(description='');"));
    }

    @Test
    void testGetLiquibaseChangeSetSql_TableDescriptionIsOnlyInDedicatedDescChangeset() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createSamplePhysicalTable(), "dataset");

        assertFalse(sql.contains(") OPTIONS(description='Test table description');"));
        assertFalse(sql.contains("ADD COLUMN IF NOT EXISTS city STRING OPTIONS(description='City name')"));

        assertTrue(sql.contains("--changeset fusadora:test_table_desc runOnChange:true"));
        assertEquals(1, countOccurrences(sql, "ALTER TABLE dataset.test_table SET OPTIONS(description='Test table description');"));
        assertEquals(1, countOccurrences(sql, "ALTER TABLE dataset.test_table ALTER COLUMN id SET OPTIONS(description='Primary key');"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.test_table SET OPTIONS(description='');"));
    }

    @Test
    void testGetLiquibaseChangeSetSql_DescChangesetStillExistsWhenDescriptionsAreAbsent() {
        PhysicalField id = new PhysicalField();
        id.setName("id");
        id.setType("INT64");
        id.setChangeSetNumber(1);

        PhysicalTable table = new PhysicalTable();
        table.setName("no_desc_table");
        // No description set — _1_desc changeset must not be generated
        table.setPhysicalFields(List.of(id));
        table.setCurrentChangeSetNumber(1);

        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(table, "dataset");

        assertTrue(sql.contains("--changeset fusadora:no_desc_table_1"));
        assertTrue(sql.contains("--changeset fusadora:no_desc_table_desc runOnChange:true"));
        assertTrue(sql.contains("ALTER TABLE dataset.no_desc_table SET OPTIONS(description='');"));
        assertTrue(sql.contains("ALTER TABLE dataset.no_desc_table ALTER COLUMN id SET OPTIONS(description='');"));
    }

    @Test
    void testGetLiquibaseChangeSetSql_DescChangesetAppearsAfterAllStructuralChanges() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createSamplePhysicalTable(), "dataset");

        int createPos = sql.indexOf("--changeset fusadora:test_table_1" + System.lineSeparator());
        int alterPos2 = sql.indexOf("--changeset fusadora:test_table_2");
        int alterPos3 = sql.indexOf("--changeset fusadora:test_table_3");
        int descPos   = sql.indexOf("--changeset fusadora:test_table_desc runOnChange:true");

        assertTrue(createPos >= 0, "CREATE TABLE changeset must exist");
        assertTrue(alterPos2 >= 0, "ALTER changeset _2 must exist");
        assertTrue(alterPos3 >= 0, "ALTER changeset _3 must exist");
        assertTrue(descPos   >= 0, "Description changeset must exist");

        assertTrue(createPos < alterPos2, "CREATE TABLE changeset must come before ALTER changesets");
        assertTrue(alterPos2 < alterPos3, "ALTER changesets must stay ordered");
        assertTrue(alterPos3 < descPos, "Description changeset must come after all structural changesets");
    }

    @Test
    void testGetLiquibaseChangeSetSql_RunOnChangeIsOnlyOnDescChangeset() {
        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(createSamplePhysicalTable(), "dataset");

        // Exactly one runOnChange:true — only the stable description changeset
        assertEquals(1, countOccurrences(sql, "runOnChange:true"));
        assertTrue(sql.contains("--changeset fusadora:test_table_desc runOnChange:true"));

        // Structural changesets must NOT carry runOnChange
        assertFalse(sql.contains("--changeset fusadora:test_table_1 runOnChange"));
        assertFalse(sql.contains("--changeset fusadora:test_table_2 runOnChange"));
        assertFalse(sql.contains("--changeset fusadora:test_table_3 runOnChange"));
    }

    @Test
    void testGetLiquibaseChangeSetSql_DescChangesetRollbackClearsDescription() {
        PhysicalField id = new PhysicalField();
        id.setName("id");
        id.setType("INT64");
        id.setChangeSetNumber(1);

        PhysicalTable table = new PhysicalTable();
        table.setName("rollback_desc_table");
        table.setDescription("My table description");
        table.setPhysicalFields(List.of(id));
        table.setCurrentChangeSetNumber(1);

        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(table, "dataset");

        assertTrue(sql.contains("ALTER TABLE dataset.rollback_desc_table SET OPTIONS(description='My table description');"));
        assertTrue(sql.contains("ALTER TABLE dataset.rollback_desc_table ALTER COLUMN id SET OPTIONS(description='');"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.rollback_desc_table SET OPTIONS(description='');"));
    }

    @Test
    void testGetLiquibaseChangeSetSql_SingleChangesetTableWithDescription() {
        PhysicalField id = new PhysicalField();
        id.setName("id");
        id.setType("INT64");
        id.setChangeSetNumber(1);
        id.setDescription("Unique identifier");

        PhysicalTable table = new PhysicalTable();
        table.setName("simple_table");
        table.setDescription("A simple table");
        table.setPhysicalFields(List.of(id));
        table.setCurrentChangeSetNumber(1);

        String sql = LiquibaseChangeSetUtil.getLiquibaseChangeSetSql(table, "dataset");

        // CREATE TABLE — fully clean, no description OPTIONS anywhere
        assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS dataset.simple_table"));
        assertTrue(sql.contains("id INT64"));
        assertFalse(sql.contains("id INT64 OPTIONS(description='Unique identifier')"));
        assertFalse(sql.contains(") OPTIONS(description='A simple table');"));
        assertTrue(sql.contains("--rollback DROP TABLE IF EXISTS dataset.simple_table;"));

        // Descriptions live in dedicated mutable _desc changeset
        assertTrue(sql.contains("--changeset fusadora:simple_table_desc runOnChange:true"));
        assertTrue(sql.contains("ALTER TABLE dataset.simple_table SET OPTIONS(description='A simple table');"));
        assertTrue(sql.contains("ALTER TABLE dataset.simple_table ALTER COLUMN id SET OPTIONS(description='Unique identifier');"));
        assertTrue(sql.contains("--rollback ALTER TABLE dataset.simple_table SET OPTIONS(description='');"));

        // No ALTER TABLE for columns — only one changeset
        assertFalse(sql.contains("ADD COLUMN IF NOT EXISTS"));
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
        assertTrue(content.contains("--changeset fusadora:test_table_desc runOnChange:true"));
        assertTrue(content.contains("ALTER TABLE test_dataset.test_table ALTER COLUMN id SET OPTIONS(description='Primary key');"));

        // Cleanup
        Files.deleteIfExists(expectedFile);
        Files.deleteIfExists(tempDir.resolve(projectId).resolve(dataProductVersion).resolve(dataSetName));
        Files.deleteIfExists(tempDir.resolve(projectId).resolve(dataProductVersion));
        Files.deleteIfExists(tempDir.resolve(projectId));
        Files.deleteIfExists(tempDir);
    }
}
