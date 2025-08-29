package org.fusadora.liquibase.services;

import org.fusadora.model.datacontract.DataContract;

public interface LiquibaseService {
    void generateChangeLog(String contractId);
    void applyChangeLog(String contractId);
}
