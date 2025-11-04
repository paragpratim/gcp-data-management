package com.fusadora.gcp.repository;

import com.fusadora.model.datacontract.GCPProjects;
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GCPRepository extends DatastoreRepository<GCPProjects, Long> {
}
