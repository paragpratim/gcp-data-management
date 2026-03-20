package com.fusadora.model.validator.context;

import com.fusadora.model.datacontract.PhysicalField;

/**
 * com.fusadora.model.validator.context.FieldValidationContext
 * Immutable context for validation of a physical field during recursive traversal.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
public record FieldValidationContext(String tableName, String fieldPath, PhysicalField field) {

}


