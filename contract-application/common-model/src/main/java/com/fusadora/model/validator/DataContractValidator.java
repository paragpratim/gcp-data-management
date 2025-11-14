package com.fusadora.model.validator;

import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalTable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataContractValidator implements ConstraintValidator<ValidDataContract, DataContract> {

    @Override
    @SuppressWarnings("java:S3776") // Suppress cognitive complexity warning
    public boolean isValid(DataContract contract, ConstraintValidatorContext context) {
        if (contract == null) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        // Validate table names are unique
        if (contract.getPhysicalModel() != null && contract.getPhysicalModel().getPhysicalTables() != null) {
            List<PhysicalTable> tables = contract.getPhysicalModel().getPhysicalTables();
            Set<String> tableNames = new HashSet<>();

            for (PhysicalTable table : tables) {
                if (table.getName() != null) {
                    if (!tableNames.add(table.getName().toLowerCase())) {
                        context.buildConstraintViolationWithTemplate(
                                        "Duplicate table name found: " + table.getName())
                                .addPropertyNode("physicalModel.tables")
                                .addConstraintViolation();
                        isValid = false;
                    }

                    // Validate column names are unique within each table
                    if (table.getPhysicalFields() != null) {
                        Set<String> columnNames = new HashSet<>();
                        for (PhysicalField column : table.getPhysicalFields()) {
                            if (column.getName() != null) {
                                if (!columnNames.add(column.getName().toLowerCase())) {
                                    context.buildConstraintViolationWithTemplate(
                                                    "Duplicate column name '" + column.getName() +
                                                            "' in table: " + table.getName())
                                            .addPropertyNode("physicalModel.tables[].columns")
                                            .addConstraintViolation();
                                    isValid = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return isValid;
    }
}