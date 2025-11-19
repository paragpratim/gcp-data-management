package com.fusadora.contract.service;

import com.fusadora.contract.repository.ContractRepository;
import com.fusadora.contract.utils.DataContractChangeSetUtil;
import com.fusadora.contract.utils.DataContractVersionUtil;
import com.fusadora.model.datacontract.DataContract;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * com.fusadora.contract.service.ContractServiceImpl
 * Implementation of the ContractService interface for managing DataContract entities.
 * Provides methods to save, retrieve, delete, update, and validate contracts,
 * as well as checking for existence and retrieving all contracts.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */
@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private Validator validator;

    /**
     * Saves a new DataContract after initializing change set numbers and setting the next version.
     *
     * @param contract the DataContract to be saved
     * @throws IllegalArgumentException if the contract is null
     */
    @Override
    public void saveContract(DataContract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }

        // Initialize change set numbers for physical tables and fields
        DataContractChangeSetUtil.addChangeSetNumber(contract);

        contractRepository.save(DataContractVersionUtil.addNextVersion(contract));
    }

    /**
     * Retrieves a DataContract by its ID.
     *
     * @param contractId the ID of the contract to retrieve
     * @return the DataContract with the specified ID
     * @throws IllegalArgumentException if the contract ID format is invalid or the contract is not found
     */
    @Override
    public DataContract getContract(String contractId) {
        try {
            Long id = Long.parseLong(contractId);
            return contractRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + contractId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid contract ID format: " + contractId, e);
        }
    }

    /**
     * Deletes a DataContract by its ID.
     *
     * @param contractId the ID of the contract to delete
     * @throws IllegalArgumentException if the contract ID format is invalid or the contract is not found
     */
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

    /**
     * Updates an existing DataContract.
     *
     * @param contract the DataContract to be updated
     * @throws IllegalArgumentException if the contract is null or not found
     */
    @Override
    public void updateContract(DataContract contract) {
        if (!contractRepository.existsById(contract.getContractId())) {
            throw new IllegalArgumentException("Contract not found with id: " + contract.getContractId());
        }

        // Update change set numbers for physical tables and fields
        DataContractChangeSetUtil.updateChangeSetNumber(contract);

        contractRepository.save(DataContractVersionUtil.addNextVersion(contract));
    }

    /**
     * Checks if a DataContract exists by its ID.
     *
     * @param contractId the ID of the contract to check
     * @return true if the contract exists, false otherwise
     * @throws IllegalArgumentException if the contract ID format is invalid
     */
    @Override
    public boolean contractExists(String contractId) {
        try {
            Long id = Long.parseLong(contractId);
            return contractRepository.existsById(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid contract ID format: " + contractId, e);
        }
    }

    /**
     * Validates a DataContract.
     *
     * @param contract the DataContract to be validated
     * @throws IllegalArgumentException if the contract is null or has invalid fields
     */
    @Override
    public void validateContract(DataContract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }
        if (contract.getDataProductName() == null || contract.getDataProductName().isEmpty()) {
            throw new IllegalArgumentException("Contract name cannot be null or empty");
        }
        Set<ConstraintViolation<DataContract>> violations = validator.validate(contract);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException("Contract validation failed: " + message);
        }
    }

    /**
     * Retrieves all contract IDs.
     *
     * @return a list of all contract IDs
     */
    @Override
    public List<Long> getAllContractIds() {
        return StreamSupport.stream(contractRepository.findAll().spliterator(), false).map(DataContract::getContractId).toList();
    }

    /**
     * Retrieves all DataContracts.
     *
     * @return a list of all DataContracts
     */
    @Override
    public List<DataContract> getAllContracts() {
        return StreamSupport.stream(contractRepository.findAll().spliterator(), false).toList();
    }
}
