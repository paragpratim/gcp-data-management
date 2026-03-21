package com.fusadora.model.validator.rules;

import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalTable;
import com.fusadora.model.validator.context.ValidationCollector;
import com.fusadora.model.validator.traversal.PhysicalFieldWalker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * com.fusadora.model.validator.rules.DuplicateFieldNameRule
 * Validates that field names are unique across the recursively traversed field tree of a table.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
public class DuplicateFieldNameRule implements DataContractRule {

    private static final String IN_TABLE = " in table ";

    private final PhysicalFieldWalker physicalFieldWalker;

    public DuplicateFieldNameRule(PhysicalFieldWalker physicalFieldWalker) {
        this.physicalFieldWalker = physicalFieldWalker;
    }

    @Override
    public void validate(DataContract contract, ValidationCollector validationCollector) {
        List<PhysicalTable> tables = contract.getPhysicalModel().getPhysicalTables();

        for (PhysicalTable table : tables) {
            if (table == null) {
                continue;
            }

            String tableName = resolveTableName(table.getName());
            Set<String> fieldNames = new HashSet<>();
            physicalFieldWalker.walk(table.getPhysicalFields(), tableName, fieldContext -> {
                String fieldName = fieldContext.field().getName();
                if (fieldName != null && !fieldNames.add(fieldName)) {
                    validationCollector.addViolation("Duplicate column name: " + fieldName + IN_TABLE + tableName);
                }
            });
        }
    }

    private String resolveTableName(String tableName) {
        return tableName == null ? "<unknown>" : tableName;
    }
}


