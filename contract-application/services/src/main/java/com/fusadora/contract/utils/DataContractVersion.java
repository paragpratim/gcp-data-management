package com.fusadora.contract.utils;

import com.fusadora.contract.repository.ContractRepository;
import com.fusadora.model.datacontract.DataContract;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;


public class DataContractVersion {


    @Transactional
    public static DataContract createWithNextVersion(DataContract aDataContract, ContractRepository contractRepository) {
        Double next = computeNextVersion(aDataContract.getContractId(), contractRepository);
        aDataContract.setVersion(next);
        return aDataContract;
    }

    private static Double computeNextVersion(Long dataContractId, ContractRepository contractRepository) {
        if (dataContractId == null) {
            return 0.1d;
        }

        Optional<DataContract> latestContract = contractRepository.findById(dataContractId);
        if (latestContract.isEmpty()) {
            return 0.1d;
        }

        BigDecimal latestVersion = BigDecimal.valueOf(latestContract.get().getVersion())
                .add(BigDecimal.valueOf(0.1d))
                .setScale(1, RoundingMode.HALF_UP);
        return latestVersion.doubleValue();
    }
}
