package com.fusadora.gcp.controller;

import com.fusadora.gcp.service.GCPService;
import com.fusadora.model.datacontract.BigQueryDataset;
import com.fusadora.model.datacontract.GCPProjects;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/gcp")
@CrossOrigin(origins = "*")
public class GCPController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(GCPController.class);

    @Autowired
    private GCPService gcpService;

    @PostMapping("/saveProject")
    public String saveProject(@RequestBody GCPProjects gcpProjects) {
        try {
            gcpService.addGCPProject(gcpProjects);
            return "GCP Project saved successfully";
        } catch (Exception e) {
            logger.error("Error saving GCP Project: ", e);
            return "Error saving GCP Project: " + e.getMessage();
        }
    }

    @GetMapping("/getProjects")
    public List<GCPProjects> getProjects() {
        return gcpService.getGCPProjects();
    }

    @GetMapping("/getBigQueryDatasets/{gcpProjectId}")
    public List<BigQueryDataset> getBigQueryDatasets(@PathVariable String gcpProjectId) {
        List<BigQueryDataset> datasets = new ArrayList<>();
        try {
            datasets = gcpService.getBigQueryDatasets(gcpProjectId);
        } catch (Exception e) {
            logger.error("Error retrieving BigQuery datasets: ", e);
        }
        return datasets;
    }
}
