package com.fusadora.contract.service;

import com.fusadora.contract.repository.ContractRepository;
import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalField;
import com.fusadora.model.datacontract.PhysicalTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public void saveContract(DataContract contract) {
        int initialChangeSetNumber = 1;
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }

        List<PhysicalTable> physicalTables = contract.getPhysicalModel() != null
                ? Optional.ofNullable(contract.getPhysicalModel().getPhysicalTables()).orElse(Collections.emptyList())
                : Collections.emptyList();
        for (PhysicalTable table : physicalTables) {
            table.setCurrentChangeSetNumber(initialChangeSetNumber);

            List<PhysicalField> physicalFields = Optional.ofNullable(table.getPhysicalFields()).orElse(Collections.emptyList());
            for (PhysicalField field : physicalFields) {
                field.setChangeSetNumber(initialChangeSetNumber);
            }
        }

        contractRepository.save(contract);
    }

    @Override
    public DataContract getContract(String contractId) {
        try {
            Long id = Long.parseLong(contractId);
            return contractRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + contractId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid contract ID format: " + contractId, e);
        }
    }

    @Override
    public void deleteContract(String contractId) {
        try {
            Long id = Long.parseLong(contractId);
            if (!contractRepository.existsById(id)) {
                throw new IllegalArgumentException("Contract not found with id: " + contractId);
            }
            contractRepository.deleteById(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid contract ID format: " + contractId, e);
        }
    }

    @Override
    public void updateContract(DataContract contract) {
        if (!contractRepository.existsById(contract.getContractId())) {
            throw new IllegalArgumentException("Contract not found with id: " + contract.getContractId());
        }

        List<PhysicalTable> physicalTables = contract.getPhysicalModel() != null
                ? Optional.ofNullable(contract.getPhysicalModel().getPhysicalTables()).orElse(Collections.emptyList())
                : Collections.emptyList();
        for (PhysicalTable table : physicalTables) {
            int currentChangeSetNumber = table.getCurrentChangeSetNumber();

            List<PhysicalField> physicalFields = Optional.ofNullable(table.getPhysicalFields()).orElse(Collections.emptyList());
            for (PhysicalField field : physicalFields) {
                if (field.getChangeSetNumber() != currentChangeSetNumber) {
                    field.setChangeSetNumber(currentChangeSetNumber + 1);
                }
            }
            table.setCurrentChangeSetNumber(currentChangeSetNumber + 1);
        }

        contractRepository.save(contract);
    }

    @Override
    public boolean contractExists(String contractId) {
        try {
            Long id = Long.parseLong(contractId);
            return contractRepository.existsById(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid contract ID format: " + contractId, e);
        }
    }

    @Override
    public void validateContract(DataContract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }
        if (contract.getDataProductName() == null || contract.getDataProductName().isEmpty()) {
            throw new IllegalArgumentException("Contract name cannot be null or empty");
        }
    }

    @Override
    public List<Long> getAllContractIds() {
        return StreamSupport.stream(contractRepository.findAll().spliterator(), false).map(DataContract::getContractId).toList();
    }

    @Override
    public List<DataContract> getAllContracts() {
        return StreamSupport.stream(contractRepository.findAll().spliterator(), false).toList();
    }
}
