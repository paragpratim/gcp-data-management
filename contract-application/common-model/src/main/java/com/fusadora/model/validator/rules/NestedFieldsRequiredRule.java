package com.fusadora.model.validator.rules;

import com.fusadora.model.datacontract.BigQueryFieldType;
import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalTable;
import com.fusadora.model.validator.context.ValidationCollector;
import com.fusadora.model.validator.traversal.PhysicalFieldWalker;

import java.util.List;
import java.util.Optional;

/**
 * com.fusadora.model.validator.rules.NestedFieldsRequiredRule
 * Validates that fields of type STRUCT or ARRAY have nested_fields defined.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
public class NestedFieldsRequiredRule implements DataContractRule {

    private static final String IN_TABLE = " in table ";

    private final PhysicalFieldWalker physicalFieldWalker;

    public NestedFieldsRequiredRule(PhysicalFieldWalker physicalFieldWalker) {
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
                Optional<BigQueryFieldType> type = BigQueryFieldType.fromValue(fieldContext.field().getType());
                if (type.isPresent()
                        && (type.get() == BigQueryFieldType.STRUCT || type.get() == BigQueryFieldType.ARRAY)
                        && (fieldContext.field().getNestedFields() == null
                        || fieldContext.field().getNestedFields().isEmpty())) {
                    validationCollector.addViolation("Field " + fieldContext.fieldPath() + IN_TABLE + tableName
                            + " must define nested_fields for type " + type.get().name());
                }
            });
        }
    }

    private String resolveTableName(String tableName) {
        return tableName == null ? "<unknown>" : tableName;
    }
}


