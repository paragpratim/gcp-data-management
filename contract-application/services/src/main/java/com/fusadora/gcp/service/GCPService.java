package com.fusadora.gcp.service;

import com.fusadora.model.datacontract.BigQueryDataset;
import com.fusadora.model.datacontract.GCPProjects;

import java.util.List;

public interface GCPService {

    public void addGCPProject(GCPProjects gcpProjectId);
    public void deleteGCPProject(String gcpProjectId);
    public List<GCPProjects> getGCPProjects();
    public List<BigQueryDataset> getBigQueryDatasets(String gcpProjectId);
}
