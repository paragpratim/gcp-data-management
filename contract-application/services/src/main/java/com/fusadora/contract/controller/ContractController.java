package com.fusadora.contract.controller;

import com.fusadora.contract.service.ContractService;
import com.fusadora.model.datacontract.DataContract;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * com.fusadora.contract.controller.ContractController
 * This controller handles HTTP requests related to data contracts,
 * including saving, retrieving, deleting, updating, and validating contracts.
 * It uses ContractService to perform the necessary business logic.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "*")
public class ContractController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ContractController.class);

    @Autowired
    private ContractService contractService;

    /**
     * Save a new data contract.
     *
     * @param contract The DataContract object to be saved.
     * @return A success message or error message.
     */
    @PostMapping("/save")
    public String saveContract(@RequestBody DataContract contract) {
        try {
            contractService.saveContract(contract);
            return String.format("Contract [%d] saved successfully", contract.getContractId());
        } catch (Exception e) {
            logger.error("Error saving contract: ", e);
            return "Error saving contract: " + e.getMessage();
        }
    }

    /**
     * Retrieve a data contract by its ID.
     *
     * @param contractId The ID of the contract to retrieve.
     * @return The DataContract object.
     */
    @GetMapping("/get/{contractId}")
    public DataContract getContract(@PathVariable String contractId) {
        return contractService.getContract(contractId);
    }

    /**
     * Delete a data contract by its ID.
     *
     * @param contractId The ID of the contract to delete.
     * @return A success message or error message.
     */
    @DeleteMapping("/delete/{contractId}")
    public String deleteContract(@PathVariable String contractId) {
        try {
            contractService.deleteContract(contractId);
            return "Contract deleted successfully";
        } catch (Exception e) {
            logger.error("Error deleting contract: ", e);
            return "Error deleting contract: " + e.getMessage();
        }
    }

    /**
     * Update an existing data contract.
     *
     * @param contract The DataContract object with updated information.
     * @return A success message or error message.
     */
    @PutMapping("/update")
    public String updateContract(@RequestBody DataContract contract) {
        try {
            contractService.updateContract(contract);
            return String.format("Contract [%d] updated successfully", contract.getContractId());
        } catch (Exception e) {
            logger.error("Error updating contract: ", e);
            return "Error updating contract: " + e.getMessage();
        }
    }

    /**
     * Check if a contract exists by its ID.
     *
     * @param contractId The ID of the contract to check.
     * @return True if the contract exists, false otherwise.
     */
    @GetMapping("/exists/{contractId}")
    public boolean contractExists(@PathVariable String contractId) {
        return contractService.contractExists(contractId);
    }

    /**
     * Retrieve all contract IDs.
     *
     * @return A list of all contract IDs.
     */
    @GetMapping("/all-ids")
    public List<Long> getAllContractIds() {
        return contractService.getAllContractIds();
    }

    /**
     * Retrieve all data contracts.
     *
     * @return A list of all DataContract objects.
     */
    @GetMapping("/all")
    public List<DataContract> getAllContracts() {
        return contractService.getAllContracts();
    }

    /**
     * Validate a data contract.
     *
     * @param contract The DataContract object to validate.
     * @return A success message or error message.
     */
    @PostMapping("/validate")
    public String validateContract(@RequestBody DataContract contract) {
        try {
            contractService.validateContract(contract);
            return "Contract is valid";
        } catch (Exception e) {
            return "Contract validation failed: " + e.getMessage();
        }
    }


}
