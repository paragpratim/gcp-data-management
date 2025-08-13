package org.fusadora.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "ingestion_target")
public class Target implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("target_id")
    @Column(name = "target_id", nullable = false, unique = true)
    private Long targetId;

    @Column(name = "project", nullable = false)
    @JsonProperty("project")
    private String project;

    @Column(name = "bigquery_dataset", nullable = false)
    @JsonProperty("bigquery_dataset")
    private String bigqueryDataset;

    @Column(name = "bigquery_table", nullable = false)
    @JsonProperty("bigquery_table")
    private String bigqueryTable;

    // Getters and setters

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

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
