package org.fusadora.contract.service;

import org.fusadora.model.IngestionContract;

import java.util.List;

public interface ContractService {

    public void saveContract(IngestionContract contract);
    public IngestionContract getContract(String contractId);
    public void deleteContract(String contractId);
    public void updateContract(IngestionContract contract);
    public boolean contractExists(String contractId);
    public void validateContract(IngestionContract contract);
    public List<Long> getAllContractIds();
    public List<IngestionContract> getAllContracts();
}
