package org.fusadora.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Target {
    @JsonProperty("project")
    private String project;

    @JsonProperty("bigquery_dataset")
    private String bigqueryDataset;

    @JsonProperty("bigquery_table")
    private String bigqueryTable;

    // Getters and setters
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBigqueryDataset() {
        return bigqueryDataset;
    }

    public void setBigqueryDataset(String bigqueryDataset) {
        this.bigqueryDataset = bigqueryDataset;
    }

    public String getBigqueryTable() {
        return bigqueryTable;
    }

    public void setBigqueryTable(String bigqueryTable) {
        this.bigqueryTable = bigqueryTable;
    }
}
