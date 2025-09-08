package org.fusadora.liquibase.services;

public interface LiquibaseService {
    void generateChangeLog(String contractId);

    void applyChangeLog(String contractId);
}
