package com.fusadora.model.validator;

import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalModel;
import com.fusadora.model.datacontract.PhysicalTable;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

class DataContractValidatorTest {

    @Test
    void isValid_returnsTrue_whenDataContractIsNull() {
        DataContractValidator validator = new DataContractValidator();
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_returnsTrue_whenPhysicalModelIsNull() {
        DataContractValidator validator = new DataContractValidator();
        DataContract contract = new DataContract();
        contract.setPhysicalModel(null);
        assertTrue(validator.isValid(contract, null));
    }

    @Test
    void isValid_returnsTrue_whenPhysicalTablesAreEmpty() {
        DataContractValidator validator = new DataContractValidator();
        DataContract contract = new DataContract();
        contract.setPhysicalModel(new PhysicalModel());
        contract.getPhysicalModel().setPhysicalTables(List.of());
        assertTrue(validator.isValid(contract, null));
    }

    @Test
    void isValid_returnsFalse_whenDuplicateTableNamesExist() {
        DataContractValidator validator = new DataContractValidator();
        DataContract contract = new DataContract();
        PhysicalTable table1 = new PhysicalTable();
        table1.setName("Table1");
        PhysicalTable table2 = new PhysicalTable();
        table2.setName("Table1");
        contract.setPhysicalModel(new PhysicalModel());
        contract.getPhysicalModel().setPhysicalTables(List.of(table1, table2));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));
        assertFalse(validator.isValid(contract, context));
    }

    @Test
    void isValid_returnsFalse_whenDuplicateColumnNamesExistInTable() {
        DataContractValidator validator = new DataContractValidator();
        DataContract contract = new DataContract();
        PhysicalField column1 = new PhysicalField();
        column1.setName("Column1");
        PhysicalField column2 = new PhysicalField();
        column2.setName("Column1");
        PhysicalTable table = new PhysicalTable();
        table.setName("Table1");
        table.setPhysicalFields(List.of(column1, column2));
        contract.setPhysicalModel(new PhysicalModel());
        contract.getPhysicalModel().setPhysicalTables(List.of(table));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));
        assertFalse(validator.isValid(contract, context));
    }

    @Test
    void isValid_returnsTrue_whenAllTableAndColumnNamesAreUnique() {
        DataContractValidator validator = new DataContractValidator();
        DataContract contract = new DataContract();
        PhysicalField column1 = new PhysicalField();
        column1.setName("Column1");
        PhysicalField column2 = new PhysicalField();
        column2.setName("Column2");
        PhysicalTable table1 = new PhysicalTable();
        table1.setName("Table1");
        table1.setPhysicalFields(List.of(column1));
        PhysicalTable table2 = new PhysicalTable();
        table2.setName("Table2");
        table2.setPhysicalFields(List.of(column2));
        contract.setPhysicalModel(new PhysicalModel());
        contract.getPhysicalModel().setPhysicalTables(List.of(table1, table2));
        assertTrue(validator.isValid(contract, null));
    }
}