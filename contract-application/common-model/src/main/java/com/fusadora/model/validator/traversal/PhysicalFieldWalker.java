package com.fusadora.model.validator.traversal;

import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.validator.context.FieldValidationContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * com.fusadora.model.validator.traversal.PhysicalFieldWalker
 * Walks physical fields recursively and emits a validation context for each field.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
public class PhysicalFieldWalker {

    public void walk(List<PhysicalField> fields, String tableName, Consumer<FieldValidationContext> consumer) {
        walk(fields, tableName, null, consumer);
    }

    private void walk(List<PhysicalField> fields,
                      String tableName,
                      String parentFieldPath,
                      Consumer<FieldValidationContext> consumer) {
        if (fields == null || fields.isEmpty()) {
            return;
        }

        for (PhysicalField field : fields) {
            if (field == null) {
                continue;
            }

            String fieldPath = buildFieldPath(parentFieldPath, field.getName());
            consumer.accept(new FieldValidationContext(tableName, fieldPath, field));
            walk(field.getNestedFields(), tableName, fieldPath, consumer);
        }
    }

    private String buildFieldPath(String parentFieldPath, String fieldName) {
        String currentFieldName = (fieldName == null || fieldName.isBlank()) ? "<unknown>" : fieldName;
        if (parentFieldPath == null || parentFieldPath.isBlank()) {
            return currentFieldName;
        }
        return parentFieldPath + "." + currentFieldName;
    }
}


