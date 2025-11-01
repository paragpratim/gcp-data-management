package com.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;

@Entity(name = "gcp_projects")
public class GCPProjects implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @JsonProperty("gcp_project_record_id")
    @Field(name = "gcp_project_record_id")
    private Long gcpProjectRecordId;

    @JsonProperty("gcp_project_id")
    @Field(name = "gcp_project_id")
    private String gcpProjectId;

    //getters and setters
    public Long getGcpProjectRecordId() {
        return gcpProjectRecordId;
    }

    public void setGcpProjectRecordId(Long gcpProjectRecordId) {
        this.gcpProjectRecordId = gcpProjectRecordId;
    }

    public String getGcpProjectId() {
        return gcpProjectId;
    }

    public void setGcpProjectId(String gcpProjectId) {
        this.gcpProjectId = gcpProjectId;
    }
}
