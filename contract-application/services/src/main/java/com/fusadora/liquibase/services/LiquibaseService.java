package com.fusadora.liquibase.services;

public interface LiquibaseService {
    String generateChangeLog(String contractId);

    String applyChangeLog(String contractId);
}
