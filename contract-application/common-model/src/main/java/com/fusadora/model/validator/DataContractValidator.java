package com.fusadora.model.validator;

import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalModel;
import com.fusadora.model.datacontract.PhysicalTable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * com.fusadora.model.validator.DataContractValidator
 * Validates that within a DataContract, all table names are unique and all column names within each table are unique.
 * Any violations will result in constraint violations being added to the validation context.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

public class DataContractValidator implements ConstraintValidator<ValidDataContract, DataContract> {

    @Override
    @SuppressWarnings("java:S3776") // Suppress cognitive complexity warning
    public boolean isValid(DataContract contract, ConstraintValidatorContext context) {
        if (contract == null || contract.getPhysicalModel() == null) {
            return true;
        }

        PhysicalModel model = contract.getPhysicalModel();
        List<PhysicalTable> tables = model.getPhysicalTables();
        if (tables == null || tables.isEmpty()) {
            return true;
        }

        List<String> violations = new ArrayList<>();

        // check duplicate table names
        Set<String> tableNames = new HashSet<>();
        for (PhysicalTable table : tables) {
            if (table == null) {
                continue;
            }
            String tName = table.getName();
            if (tName != null && !tableNames.add(tName)) {
                violations.add("Duplicate table name: " + tName);
            }

            // check duplicate column names within this table
            List<PhysicalField> fields = table.getPhysicalFields();
            if (fields != null) {
                Set<String> fieldNames = new HashSet<>();
                for (PhysicalField field : fields) {
                    if (field == null) {
                        continue;
                    }
                    String fName = field.getName();
                    if (fName != null && !fieldNames.add(fName)) {
                        violations.add("Duplicate column name: " + fName + " in table " + (tName == null ? "<unknown>" : tName));
                    }
                }
            }
        }

        if (!violations.isEmpty()) {
            if (context != null) {
                context.disableDefaultConstraintViolation();
                for (String msg : violations) {
                    context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
                }
            }
            return false;
        }

        return true;
    }
}