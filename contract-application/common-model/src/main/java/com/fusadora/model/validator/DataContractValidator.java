package com.fusadora.model.validator;

import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.BigQueryFieldType;
import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalModel;
import com.fusadora.model.datacontract.PhysicalTable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

    private static final String IN_TABLE = " in table ";

    /**
     * Recursively validates field names for uniqueness and checks for valid types and nested fields.
     *
     * @param fields Fields to validate
     * @param tableName String representing the name of the table for error messages
     * @param fieldNames Set to track unique field names within the current table
     * @param violations List to collect any validation violation messages
     */
    private void validateFieldNamesRecursively(List<PhysicalField> fields,
                                               String tableName,
                                               Set<String> fieldNames,
                                               List<String> violations) {
        if (fields == null || fields.isEmpty()) {
            return;
        }

        for (PhysicalField field : fields) {
            if (field == null) {
                continue;
            }

            String fName = field.getName();
            if (fName != null && !fieldNames.add(fName)) {
                violations.add("Duplicate column name: " + fName + IN_TABLE + tableName);
            }

            String fieldPath = (fName == null || fName.isBlank()) ? "<unknown>" : fName;
            validateTypeAndNestedFieldsRecursively(field, tableName, fieldPath, violations);

            validateFieldNamesRecursively(field.getNestedFields(), tableName, fieldNames, violations);
        }
    }

    /**
     * Validates that the field type is supported and that STRUCT/ARRAY types have nested fields defined.
     * Recursively validates nested fields as well.
     *
     * @param field PhysicalField to validate
     * @param tableName String representing the name of the table for error messages
     * @param fieldPath String representing the path to the current field for error messages
     * @param violations List to collect any validation violation messages
     */
    private void validateTypeAndNestedFieldsRecursively(PhysicalField field,
                                                        String tableName,
                                                        String fieldPath,
                                                        List<String> violations) {
        String type = field.getType();
        if (!BigQueryFieldType.isSupported(type)) {
            violations.add("Invalid field type: " + type + " for field " + fieldPath + IN_TABLE + tableName);
            return;
        }

        Optional<BigQueryFieldType> bigQueryFieldType = BigQueryFieldType.fromValue(type);
        if (bigQueryFieldType.isPresent()
                && (bigQueryFieldType.get() == BigQueryFieldType.STRUCT || bigQueryFieldType.get() == BigQueryFieldType.ARRAY)
                && (field.getNestedFields() == null || field.getNestedFields().isEmpty())) {
            violations.add("Field " + fieldPath + IN_TABLE + tableName + " must define nested_fields for type " + bigQueryFieldType.get().name());
        }

        if (field.getNestedFields() == null || field.getNestedFields().isEmpty()) {
            return;
        }

        for (PhysicalField nestedField : field.getNestedFields()) {
            if (nestedField == null) {
                continue;
            }
            String nestedName = nestedField.getName();
            String nestedPath = (nestedName == null || nestedName.isBlank()) ? fieldPath + ".<unknown>" : fieldPath + "." + nestedName;
            validateTypeAndNestedFieldsRecursively(nestedField, tableName, nestedPath, violations);
        }
    }

    /**
     * Validates the DataContract for unique table names and unique column names within each table.
     * If any violations are found, they are added to the ConstraintValidatorContext.
     * The method returns true if the DataContract is valid, and false if there are any violations.
     *
     * @param contract A DataContract object to validate
     * @param context Context for adding constraint violations if the DataContract is invalid
     * @return boolean indicating whether the DataContract is valid
     */
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
                String tableName = tName == null ? "<unknown>" : tName;
                validateFieldNamesRecursively(fields, tableName, fieldNames, violations);
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