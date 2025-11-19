package com.fusadora.contract.utils;

import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalTable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * com.fusadora.contract.utils.DataContractChangeSetUtil
 * Utility class for managing change set numbers in DataContract objects.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

public class DataContractChangeSetUtil {

    DataContractChangeSetUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void addChangeSetNumber(DataContract aDataContract) {
        int initialChangeSetNumber = 1;
        List<PhysicalTable> physicalTables = aDataContract.getPhysicalModel() != null
                ? Optional.ofNullable(aDataContract.getPhysicalModel().getPhysicalTables()).orElse(Collections.emptyList())
                : Collections.emptyList();
        for (PhysicalTable table : physicalTables) {
            boolean hasFieldChanges = false;

            List<PhysicalField> physicalFields = Optional.ofNullable(table.getPhysicalFields()).orElse(Collections.emptyList());
            for (PhysicalField field : physicalFields) {
                field.setChangeSetNumber(initialChangeSetNumber);
                hasFieldChanges = true;
            }
            if (hasFieldChanges) {
                table.setCurrentChangeSetNumber(initialChangeSetNumber);
            }
        }
    }

    public static void updateChangeSetNumber(DataContract aDataContract) {
        List<PhysicalTable> physicalTables = aDataContract.getPhysicalModel() != null
                ? Optional.ofNullable(aDataContract.getPhysicalModel().getPhysicalTables()).orElse(Collections.emptyList())
                : Collections.emptyList();
        for (PhysicalTable table : physicalTables) {
            int currentChangeSetNumber = table.getCurrentChangeSetNumber();
            boolean hasChanges = false;

            List<PhysicalField> physicalFields = Optional.ofNullable(table.getPhysicalFields()).orElse(Collections.emptyList());
            for (PhysicalField field : physicalFields) {
                if (Objects.equals(field.getChangeSetNumber(), 0)) {
                    field.setChangeSetNumber(currentChangeSetNumber + 1);
                    hasChanges = true;
                }
            }
            if (hasChanges) {
                table.setCurrentChangeSetNumber(currentChangeSetNumber + 1);
            }
        }
    }
}
