package org.fusadora.liquibase.services;

import org.fusadora.model.datacontract.DataContract;

public interface LiquibaseService {
    void generateChangeLog(DataContract dataContract);
    void applyChangeLog(DataContract dataContract);
}
