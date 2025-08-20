package org.fusadora.contract.service;

import org.fusadora.contract.repository.ContractRepository;
import org.fusadora.model.datacontract.DataContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public void saveContract(DataContract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }
        contractRepository.save(contract);
    }

    @Override
    public DataContract getContract(String contractId) {
        try {
            Long id = Long.parseLong(contractId);
            return contractRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + contractId));
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
        return contractRepository.findAll().stream()
                .map(DataContract::getContractId)
                .toList();
    }

    @Override
    public List<DataContract> getAllContracts() {
        return contractRepository.findAll();
    }
}
