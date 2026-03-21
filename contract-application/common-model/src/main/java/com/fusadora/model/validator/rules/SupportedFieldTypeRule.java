package com.fusadora.model.validator.rules;

import com.fusadora.model.datacontract.BigQueryFieldType;
import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalTable;
import com.fusadora.model.validator.context.ValidationCollector;
import com.fusadora.model.validator.traversal.PhysicalFieldWalker;

import java.util.List;

/**
 * com.fusadora.model.validator.rules.SupportedFieldTypeRule
 * Validates that all physical fields in the data contract have supported types.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
public class SupportedFieldTypeRule implements DataContractRule {

    private static final String IN_TABLE = " in table ";

    private final PhysicalFieldWalker physicalFieldWalker;

    public SupportedFieldTypeRule(PhysicalFieldWalker physicalFieldWalker) {
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
            physicalFieldWalker.walk(table.getPhysicalFields(), tableName, fieldContext -> {
                String type = fieldContext.field().getType();
                if (!BigQueryFieldType.isSupported(type)) {
                    validationCollector.addViolation("Invalid field type: " + type
                            + " for field " + fieldContext.fieldPath() + IN_TABLE + tableName);
                }
            });
        }
    }

    private String resolveTableName(String tableName) {
        return tableName == null ? "<unknown>" : tableName;
    }
}


