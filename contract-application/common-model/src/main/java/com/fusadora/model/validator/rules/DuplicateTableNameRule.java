package com.fusadora.model.validator.rules;

import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalTable;
import com.fusadora.model.validator.context.ValidationCollector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * com.fusadora.model.validator.rules.DuplicateTableNameRule
 * This rule checks for duplicate table names in the physical model of a data contract. It iterates through all physical tables and collects their names in a set. If a table name is encountered that already exists in the set, it adds a violation to the validation collector indicating the duplicate table name.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
public class DuplicateTableNameRule implements DataContractRule {

    @Override
    public void validate(DataContract contract, ValidationCollector validationCollector) {
        List<PhysicalTable> tables = contract.getPhysicalModel().getPhysicalTables();
        Set<String> tableNames = new HashSet<>();

        for (PhysicalTable table : tables) {
            if (table == null) {
                continue;
            }

            String tableName = table.getName();
            if (tableName != null && !tableNames.add(tableName)) {
                validationCollector.addViolation("Duplicate table name: " + tableName);
            }
        }
    }
}


