package org.fusadora.contract.service;

import org.fusadora.model.datacontract.DataContract;

import java.util.List;

public interface ContractService {

    public void saveContract(DataContract contract);
    public DataContract getContract(String contractId);
    public void deleteContract(String contractId);
    public void updateContract(DataContract contract);
    public boolean contractExists(String contractId);
    public void validateContract(DataContract contract);
    public List<Long> getAllContractIds();
    public List<DataContract> getAllContracts();
}
