package org.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;


@Entity(name = "bigquery_dataset")
public class BigQueryDataset {

    @JsonProperty("project_id")
    @Field(name = "project_id")
    private String project;

    @JsonProperty("data_set_name")
    @Field(name = "data_set_name")
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
