package org.fusadora.contract.repository;

import org.fusadora.model.IngestionContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<IngestionContract, Long> {
}
