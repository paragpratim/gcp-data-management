package com.fusadora.gcp.controller;

import com.fusadora.gcp.service.GCPService;
import com.fusadora.model.datacontract.BigQueryDataset;
import com.fusadora.model.datacontract.GCPProjects;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * com.fusadora.gcp.controller.GCPController
 * This controller handles GCP project related operations such as saving projects
 * and retrieving BigQuery datasets.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

@RestController
@RequestMapping("/api/gcp")
@CrossOrigin(origins = "*")
public class GCPController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(GCPController.class);

    @Autowired
    private GCPService gcpService;

    /**
     * Save GCP Project details
     *
     * @param gcpProjects GCPProjects object containing project details
     * @return Success or error message
     */
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

    /**
     * Retrieve all GCP Projects
     *
     * @return List of GCPProjects
     */
    @GetMapping("/getProjects")
    public List<GCPProjects> getProjects() {
        return gcpService.getGCPProjects();
    }

    /**
     * Retrieve BigQuery datasets for a given GCP project ID
     *
     * @param gcpProjectId GCP Project ID
     * @return List of BigQueryDataset
     */
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
