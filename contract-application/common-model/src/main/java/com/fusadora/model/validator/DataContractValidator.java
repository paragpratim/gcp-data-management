package com.fusadora.model.validator;

import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalModel;
import com.fusadora.model.datacontract.PhysicalTable;
import com.fusadora.model.validator.context.ValidationCollector;
import com.fusadora.model.validator.rules.DataContractRule;
import com.fusadora.model.validator.rules.DuplicateFieldNameRule;
import com.fusadora.model.validator.rules.DuplicateTableNameRule;
import com.fusadora.model.validator.rules.NestedFieldsRequiredRule;
import com.fusadora.model.validator.rules.SupportedFieldTypeRule;
import com.fusadora.model.validator.traversal.PhysicalFieldWalker;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

/**
 * com.fusadora.model.validator.DataContractValidator
 * This class implements the ConstraintValidator interface to validate a DataContract object.
 * It checks for various rules such as duplicate table names, duplicate field names, supported field types, and nested fields requirements.
 * The validation logic is implemented in separate rule classes that implement the DataContractRule interface.
 * The validator iterates through the defined rules and collects any violations found during the validation process.
 * The validation results are added to the ConstraintValidatorContext, which can be used to provide feedback to the caller.
 * Any violations will result in constraint violations being added to the validation context.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

public class DataContractValidator implements ConstraintValidator<ValidDataContract, DataContract> {

    private final List<DataContractRule> rules;

    public DataContractValidator() {
        PhysicalFieldWalker physicalFieldWalker = new PhysicalFieldWalker();
        this.rules = List.of(
                new DuplicateTableNameRule(),
                new DuplicateFieldNameRule(physicalFieldWalker),
                new SupportedFieldTypeRule(physicalFieldWalker),
                new NestedFieldsRequiredRule(physicalFieldWalker)
        );
    }

    /**
     * Validates the DataContract for various rules such as duplicate table names, duplicate field names, supported field types, and nested fields requirements.
     * The method iterates through the defined rules and collects any violations found during the validation process.
     * If any violations are found, they are added to the ConstraintValidatorContext.
     * The method returns true if the DataContract is valid, and false if there are any violations.
     *
     * @param contract A DataContract object to validate
     * @param context Context for adding constraint violations if the DataContract is invalid
     * @return boolean indicating whether the DataContract is valid
     */
    @Override
    public boolean isValid(DataContract contract, ConstraintValidatorContext context) {
        if (contract == null || contract.getPhysicalModel() == null) {
            return true;
        }

        PhysicalModel model = contract.getPhysicalModel();
        List<PhysicalTable> tables = model.getPhysicalTables();
        if (tables == null || tables.isEmpty()) {
            return true;
        }

        ValidationCollector validationCollector = new ValidationCollector();
        for (DataContractRule rule : rules) {
            rule.validate(contract, validationCollector);
        }

        if (!validationCollector.isEmpty()) {
            if (context != null) {
                context.disableDefaultConstraintViolation();
                for (String msg : validationCollector.getViolations()) {
                    context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
                }
            }
            return false;
        }

        return true;
    }
}