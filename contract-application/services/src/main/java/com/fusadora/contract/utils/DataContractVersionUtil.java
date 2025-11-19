package com.fusadora.contract.utils;

import com.fusadora.model.datacontract.DataContract;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * com.fusadora.contract.utils.DataContractVersionUtil
 * Utility class to handle versioning of DataContract objects.
 * Provides methods to compute and assign the next version number
 * based on existing contract details.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

public class DataContractVersionUtil {

    DataContractVersionUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static DataContract addNextVersion(DataContract aDataContract) {
        Double next = computeNextVersion(aDataContract);
        aDataContract.setVersion(next);
        return aDataContract;
    }

    private static Double computeNextVersion(DataContract aDataContract) {
        // New contract
        if (aDataContract.getContractId() == null || aDataContract.getVersion() == null) {
            return 0.1d;
        }

        // Existing contract
        BigDecimal latestVersion = BigDecimal.valueOf(aDataContract.getVersion())
                .add(BigDecimal.valueOf(0.1d))
                .setScale(1, RoundingMode.HALF_UP);
        return latestVersion.doubleValue();
    }
}
