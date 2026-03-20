package com.fusadora.model.validator.rules;

import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.validator.context.ValidationCollector;

/**
 * com.fusadora.model.validator.rules.DataContractRule
 * This interface defines a contract-level validation rule for a DataContract. Implementations of this interface will provide specific validation logic to ensure that a DataContract adheres to certain rules or constraints. The validate method takes a DataContract instance and a ValidationCollector to collect any validation errors or warnings that may arise during the validation process.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
public interface DataContractRule {
    void validate(DataContract contract, ValidationCollector validationCollector);
}


