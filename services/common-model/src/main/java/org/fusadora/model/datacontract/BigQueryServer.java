package org.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class BigQueryServer extends OutPutPort {

    @JsonProperty("project_id")
    @Column(name = "project_id", nullable = false)
    private String project;

    @JsonProperty("data_set_name")
    @Column(name = "data_set_name", nullable = false)
    private String dataset;

    //getters and setters
    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
