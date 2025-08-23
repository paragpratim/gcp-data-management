package org.fusadora.contract.utils;

import org.fusadora.model.datacontract.DataContract;
import org.fusadora.model.datacontract.PhysicalField;
import org.fusadora.model.datacontract.PhysicalTable;

import java.util.List;

public class SchemaUtil {

    public static String getBigQueryProjectId(DataContract aDataContract) {
        return aDataContract.getBigQueryDataset().getProject();
    }

    public static String getBigQueryDatasetId(DataContract aDataContract) {
        return aDataContract.getBigQueryDataset().getDataset();
    }

    public static List<PhysicalTable> getTables(DataContract aDataContract) {
        return aDataContract.getPhysicalModel().getPhysicalTables();
    }

    public static PhysicalTable getTableByName(DataContract aDataContract, String tableName) {
        return aDataContract.getPhysicalModel().getPhysicalTables().stream()
                .filter(t -> t.getName().equalsIgnoreCase(tableName))
                .findFirst()
                .orElse(null);
    }

    public static List<PhysicalField> getPhysicalFields(PhysicalTable aTable) {
        return aTable.getPhysicalFields();
    }

}
