package com.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;

@Entity(name = "gcp_projects")
public class GCPProjects implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("project_id")
    @Field(name = "project_id")
    private String project;

    //getters and setters
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
