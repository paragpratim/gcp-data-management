package org.fusadora.liquibase.services;

import org.fusadora.model.datacontract.DataContract;
import org.springframework.stereotype.Service;

@Service
public class LiquibaseServiceImpl implements LiquibaseService {
    @Override
    public void generateChangeLog(DataContract dataContract) {
        // TODO Add implementation
    }

    @Override
    public void applyChangeLog(DataContract dataContract) {
        // TODO Add implementation
    }
}
