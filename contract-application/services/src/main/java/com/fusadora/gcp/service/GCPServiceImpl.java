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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class GCPServiceImpl implements GCPService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(GCPServiceImpl.class);

    @Autowired
    private GCPRepository gcpRepository;

    @Override
    public void addGCPProject(GCPProjects gcpProjectId) {
        gcpRepository.save(gcpProjectId);
    }

    @Override
    public List<GCPProjects> getGCPProjects() {
        return StreamSupport.stream(gcpRepository.findAll().spliterator(), false).toList();
    }

    @Override
    public List<BigQueryDataset> getBigQueryDatasets() {
        List<BigQueryDataset> bigQueryDatasets = new java.util.ArrayList<>();
        try {
            for (GCPProjects gcpProjects : getGCPProjects()) {
                BigQuery bigQuery = BigQueryOptions.newBuilder().setProjectId(gcpProjects.getProject()).build().getService();
                for (Dataset dataset : bigQuery.listDatasets(BigQuery.DatasetListOption.all()).iterateAll()) {
                    BigQueryDataset bigQueryDataset = new BigQueryDataset();
                    bigQueryDataset.setProject(gcpProjects.getProject());
                    bigQueryDataset.setDataset(dataset.getDatasetId().getDataset());
                    bigQueryDatasets.add(bigQueryDataset);
                }
            }
        } catch (BigQueryException e) {
            logger.error("BigQueryException occurred: {}", e.getMessage());
        }
        return bigQueryDatasets;
    }
}
