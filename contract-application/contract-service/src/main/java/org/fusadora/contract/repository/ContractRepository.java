package org.fusadora.contract.repository;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import org.fusadora.model.datacontract.DataContract;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends DatastoreRepository<DataContract, Long> {
}
