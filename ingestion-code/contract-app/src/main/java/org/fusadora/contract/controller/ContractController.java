package org.fusadora.contract.controller;

import org.fusadora.contract.service.ContractService;
import org.fusadora.model.IngestionContract;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "*")
public class ContractController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ContractController.class);

    @Autowired
    private ContractService contractService;

    @PostMapping("/save")
    public String saveContract(@RequestBody IngestionContract contract) {
        try {
            contractService.saveContract(contract);
            return "Contract saved successfully";
        } catch (Exception e) {
            logger.error("Error saving contract: ", e);
            return "Error saving contract: " + e.getMessage();
        }
    }

    @GetMapping("/get/{contractId}")
    public IngestionContract getContract(@PathVariable String contractId) {
        return contractService.getContract(contractId);
    }

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

    @PutMapping("/update")
    public String updateContract(@RequestBody IngestionContract contract) {
        try {
            contractService.updateContract(contract);
            return "Contract updated successfully";
        } catch (Exception e) {
            logger.error("Error updating contract: ", e);
            return "Error updating contract: " + e.getMessage();
        }
    }

    @GetMapping("/exists/{contractId}")
    public boolean contractExists(@PathVariable String contractId) {
        return contractService.contractExists(contractId);
    }

    @GetMapping("/all-ids")
    public List<Long> getAllContractIds() {
        return contractService.getAllContractIds();
    }

    @GetMapping("/all")
    public List<IngestionContract> getAllContracts() {
        return contractService.getAllContracts();
    }

    @PostMapping("/validate")
    public String validateContract(@RequestBody IngestionContract contract) {
        try {
            contractService.validateContract(contract);
            return "Contract is valid";
        } catch (Exception e) {
            return "Contract validation failed: " + e.getMessage();
        }
    }


}
