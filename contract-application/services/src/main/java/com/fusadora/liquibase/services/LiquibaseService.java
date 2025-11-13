package com.fusadora.liquibase.services;

public interface LiquibaseService {
    String generateChangeLog(String contractId);

    void applyChangeLog(String contractId);
}
