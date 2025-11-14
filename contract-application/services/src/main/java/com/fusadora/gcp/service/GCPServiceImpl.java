package com.fusadora.gcp.service;

import com.fusadora.gcp.repository.GCPRepository;
import com.fusadora.model.datacontract.BigQueryDataset;
import com.fusadora.model.datacontract.GCPProjects;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Dataset;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
public class GCPServiceImpl implements GCPService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(GCPServiceImpl.class);
    @Value("${liquibase.change.dataset:liquibase_admin}")
    private String liquibaseChangeDataset;
    @Autowired
    private GCPRepository gcpRepository;

    @Override
    public void addGCPProject(GCPProjects gcpProjectId) {
        gcpRepository.save(gcpProjectId);
    }

    @Override
    public void deleteGCPProject(String gcpProjectId) {
        // TODO: Implement deletion logic if necessary
    }

    @Override
    public List<GCPProjects> getGCPProjects() {
        return StreamSupport.stream(gcpRepository.findAll().spliterator(), false).toList();
    }

    @Override
    public List<BigQueryDataset> getBigQueryDatasets(String gcpProjectId) {
        // Dummy data for testing purposes
        if (gcpProjectId.equals("dummy-project")) {
            BigQueryDataset bigQueryDataset = new BigQueryDataset();
            bigQueryDataset.setProject(gcpProjectId);
            bigQueryDataset.setDataset("dummy-dataset");
            return List.of(bigQueryDataset);
        }
        List<BigQueryDataset> bigQueryDatasets = new java.util.ArrayList<>();
        try {
            BigQuery bigQuery = BigQueryOptions.newBuilder().setProjectId(gcpProjectId).build().getService();
            for (Dataset dataset : bigQuery.listDatasets(BigQuery.DatasetListOption.all()).iterateAll()) {
                if (Objects.equals(dataset.getDatasetId().getDataset(), liquibaseChangeDataset))
                    continue;
                BigQueryDataset bigQueryDataset = new BigQueryDataset();
                bigQueryDataset.setProject(gcpProjectId);
                bigQueryDataset.setDataset(dataset.getDatasetId().getDataset());
                bigQueryDatasets.add(bigQueryDataset);
            }
        } catch (BigQueryException e) {
            logger.error("BigQueryException occurred: {}", e.getMessage());
        }
        return bigQueryDatasets;
    }
}
