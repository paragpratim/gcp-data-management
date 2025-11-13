package com.fusadora.contract.utils;

import com.fusadora.model.datacontract.DataContract;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class DataContractVersionUtil {

    private DataContractVersionUtil() {
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
