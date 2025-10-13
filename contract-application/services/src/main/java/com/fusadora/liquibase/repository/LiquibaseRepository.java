package com.fusadora.liquibase.repository;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import com.fusadora.model.datacontract.DataContract;
import org.springframework.stereotype.Repository;

@Repository
public interface LiquibaseRepository extends DatastoreRepository<DataContract, Long> {
}
