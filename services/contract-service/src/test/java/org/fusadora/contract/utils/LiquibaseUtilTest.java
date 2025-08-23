package org.fusadora.contract.utils;

import org.fusadora.model.datacontract.PhysicalField;
import org.fusadora.model.datacontract.PhysicalTable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LiquibaseUtilTest {

    @Test
    void testGetLiquibaseChangeSetSql_CreateTable() {
        PhysicalField field1 = new PhysicalField();
        field1.setName("id");
        field1.setType("INT64");

        PhysicalField field2 = new PhysicalField();
        field2.setName("name");
        field2.setType("STRING");

        PhysicalTable table = new PhysicalTable();
        table.setName("test_table");
        table.setPhysicalFields(Arrays.asList(field1, field2));
        table.setCurrentChangeSetNumber(1);

        String sql = LiquibaseUtil.getLiquibaseChangeSetSql(table, "dataset");

        System.out.println(sql); // For debugging purposes

        assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS dataset.test_table"));
        assertTrue(sql.contains("id INT64"));
        assertTrue(sql.contains("name STRING"));
        assertTrue(sql.contains("--rollback DROP TABLE IF EXISTS dataset.test_table;"));
    }
}
