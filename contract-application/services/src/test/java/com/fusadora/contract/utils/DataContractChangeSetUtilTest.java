
package com.fusadora.contract.utils;

import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalModel;
import com.fusadora.model.datacontract.PhysicalTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataContractChangeSetUtilTest {

    private DataContract dataContract;
    private PhysicalModel physicalModel;
    private PhysicalTable physicalTable;
    private PhysicalField physicalField1;
    private PhysicalField physicalField2;

    @BeforeEach
    void setUp() {
        dataContract = new DataContract();
        physicalModel = new PhysicalModel();
        physicalTable = new PhysicalTable();
        physicalField1 = new PhysicalField();
        physicalField2 = new PhysicalField();
    }

    @Test
    void constructor_shouldThrowException() {
        // When & Then
        assertThrows(IllegalStateException.class, DataContractChangeSetUtil::new);
    }

    @Test
    void addChangeSetNumber_shouldSetInitialChangeSetNumberForFields() {
        // Given
        physicalField1.setName("field1");
        physicalField2.setName("field2");

        List<PhysicalField> fields = new ArrayList<>();
        fields.add(physicalField1);
        fields.add(physicalField2);

        physicalTable.setPhysicalFields(fields);
        physicalModel.setPhysicalTables(Collections.singletonList(physicalTable));
        dataContract.setPhysicalModel(physicalModel);

        // When
        DataContractChangeSetUtil.addChangeSetNumber(dataContract);

        // Then
        assertEquals(1, physicalField1.getChangeSetNumber());
        assertEquals(1, physicalField2.getChangeSetNumber());
        assertEquals(1, physicalTable.getCurrentChangeSetNumber());
    }

    @Test
    void addChangeSetNumber_shouldHandleNullPhysicalModel() {
        // Given
        dataContract.setPhysicalModel(null);

        // When & Then
        assertDoesNotThrow(() -> {
            DataContractChangeSetUtil.addChangeSetNumber(dataContract);
        });
    }

    @Test
    void addChangeSetNumber_shouldHandleNullPhysicalTables() {
        // Given
        physicalModel.setPhysicalTables(null);
        dataContract.setPhysicalModel(physicalModel);

        // When & Then
        assertDoesNotThrow(() -> {
            DataContractChangeSetUtil.addChangeSetNumber(dataContract);
        });
    }

    @Test
    void addChangeSetNumber_shouldHandleEmptyPhysicalTables() {
        // Given
        physicalModel.setPhysicalTables(Collections.emptyList());
        dataContract.setPhysicalModel(physicalModel);

        // When & Then
        assertDoesNotThrow(() -> {
            DataContractChangeSetUtil.addChangeSetNumber(dataContract);
        });
    }

    @Test
    void addChangeSetNumber_shouldHandleNullPhysicalFields() {
        // Given
        physicalTable.setPhysicalFields(null);
        physicalModel.setPhysicalTables(Collections.singletonList(physicalTable));
        dataContract.setPhysicalModel(physicalModel);

        // When
        DataContractChangeSetUtil.addChangeSetNumber(dataContract);

        // Then
        assertEquals(0, physicalTable.getCurrentChangeSetNumber());
    }

    @Test
    void addChangeSetNumber_shouldHandleEmptyPhysicalFields() {
        // Given
        physicalTable.setPhysicalFields(Collections.emptyList());
        physicalModel.setPhysicalTables(Collections.singletonList(physicalTable));
        dataContract.setPhysicalModel(physicalModel);

        // When
        DataContractChangeSetUtil.addChangeSetNumber(dataContract);

        // Then
        assertEquals(0, physicalTable.getCurrentChangeSetNumber());
    }

    @Test
    void addChangeSetNumber_shouldHandleMultipleTables() {
        // Given
        PhysicalTable table2 = new PhysicalTable();
        PhysicalField field3 = new PhysicalField();
        field3.setName("field3");

        physicalField1.setName("field1");
        physicalTable.setPhysicalFields(Collections.singletonList(physicalField1));

        table2.setPhysicalFields(Collections.singletonList(field3));

        List<PhysicalTable> tables = new ArrayList<>();
        tables.add(physicalTable);
        tables.add(table2);

        physicalModel.setPhysicalTables(tables);
        dataContract.setPhysicalModel(physicalModel);

        // When
        DataContractChangeSetUtil.addChangeSetNumber(dataContract);

        // Then
        assertEquals(1, physicalField1.getChangeSetNumber());
        assertEquals(1, field3.getChangeSetNumber());
        assertEquals(1, physicalTable.getCurrentChangeSetNumber());
        assertEquals(1, table2.getCurrentChangeSetNumber());
    }

    @Test
    void updateChangeSetNumber_shouldIncrementChangeSetForNewFields() {
        // Given
        physicalField1.setChangeSetNumber(1);
        physicalField2.setChangeSetNumber(0); // New field

        List<PhysicalField> fields = new ArrayList<>();
        fields.add(physicalField1);
        fields.add(physicalField2);

        physicalTable.setPhysicalFields(fields);
        physicalTable.setCurrentChangeSetNumber(1);
        physicalModel.setPhysicalTables(Collections.singletonList(physicalTable));
        dataContract.setPhysicalModel(physicalModel);

        // When
        DataContractChangeSetUtil.updateChangeSetNumber(dataContract);

        // Then
        assertEquals(1, physicalField1.getChangeSetNumber()); // Unchanged
        assertEquals(2, physicalField2.getChangeSetNumber()); // Incremented
        assertEquals(2, physicalTable.getCurrentChangeSetNumber()); // Incremented
    }

    @Test
    void updateChangeSetNumber_shouldNotChangeWhenNoNewFields() {
        // Given
        physicalField1.setChangeSetNumber(1);
        physicalField2.setChangeSetNumber(1);

        List<PhysicalField> fields = new ArrayList<>();
        fields.add(physicalField1);
        fields.add(physicalField2);

        physicalTable.setPhysicalFields(fields);
        physicalTable.setCurrentChangeSetNumber(1);
        physicalModel.setPhysicalTables(Collections.singletonList(physicalTable));
        dataContract.setPhysicalModel(physicalModel);

        // When
        DataContractChangeSetUtil.updateChangeSetNumber(dataContract);

        // Then
        assertEquals(1, physicalField1.getChangeSetNumber());
        assertEquals(1, physicalField2.getChangeSetNumber());
        assertEquals(1, physicalTable.getCurrentChangeSetNumber()); // Unchanged
    }

    @Test
    void updateChangeSetNumber_shouldHandleNullPhysicalModel() {
        // Given
        dataContract.setPhysicalModel(null);

        // When & Then
        assertDoesNotThrow(() -> {
            DataContractChangeSetUtil.updateChangeSetNumber(dataContract);
        });
    }

    @Test
    void updateChangeSetNumber_shouldHandleNullPhysicalTables() {
        // Given
        physicalModel.setPhysicalTables(null);
        dataContract.setPhysicalModel(physicalModel);

        // When & Then
        assertDoesNotThrow(() -> {
            DataContractChangeSetUtil.updateChangeSetNumber(dataContract);
        });
    }

    @Test
    void updateChangeSetNumber_shouldHandleNullPhysicalFields() {
        // Given
        physicalTable.setPhysicalFields(null);
        physicalTable.setCurrentChangeSetNumber(1);
        physicalModel.setPhysicalTables(Collections.singletonList(physicalTable));
        dataContract.setPhysicalModel(physicalModel);

        // When
        DataContractChangeSetUtil.updateChangeSetNumber(dataContract);

        // Then
        assertEquals(1, physicalTable.getCurrentChangeSetNumber()); // Unchanged
    }

    @Test
    void updateChangeSetNumber_shouldHandleMultipleTablesWithMixedChanges() {
        // Given
        PhysicalTable table2 = new PhysicalTable();
        PhysicalField field3 = new PhysicalField();
        PhysicalField field4 = new PhysicalField();

        // Table 1: Has new fields
        physicalField1.setChangeSetNumber(1);
        physicalField2.setChangeSetNumber(0); // New field
        physicalTable.setPhysicalFields(List.of(physicalField1, physicalField2));
        physicalTable.setCurrentChangeSetNumber(1);

        // Table 2: No new fields
        field3.setChangeSetNumber(2);
        field4.setChangeSetNumber(2);
        table2.setPhysicalFields(List.of(field3, field4));
        table2.setCurrentChangeSetNumber(2);

        physicalModel.setPhysicalTables(List.of(physicalTable, table2));
        dataContract.setPhysicalModel(physicalModel);

        // When
        DataContractChangeSetUtil.updateChangeSetNumber(dataContract);

        // Then
        assertEquals(1, physicalField1.getChangeSetNumber());
        assertEquals(2, physicalField2.getChangeSetNumber());
        assertEquals(2, physicalTable.getCurrentChangeSetNumber());

        assertEquals(2, field3.getChangeSetNumber());
        assertEquals(2, field4.getChangeSetNumber());
        assertEquals(2, table2.getCurrentChangeSetNumber()); // Unchanged
    }

    @Test
    void updateChangeSetNumber_shouldHandleZeroCurrentChangeSetNumber() {
        // Given
        physicalField1.setChangeSetNumber(0); // New field

        physicalTable.setPhysicalFields(Collections.singletonList(physicalField1));
        physicalTable.setCurrentChangeSetNumber(0);
        physicalModel.setPhysicalTables(Collections.singletonList(physicalTable));
        dataContract.setPhysicalModel(physicalModel);

        // When
        DataContractChangeSetUtil.updateChangeSetNumber(dataContract);

        // Then
        assertEquals(1, physicalField1.getChangeSetNumber());
        assertEquals(1, physicalTable.getCurrentChangeSetNumber());
    }
}
