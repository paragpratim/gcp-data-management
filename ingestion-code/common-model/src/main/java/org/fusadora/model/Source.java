package org.fusadora.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "ingestion_source")
public class Source implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "source_id", nullable = false, unique = true)
    @JsonProperty("source_id")
    private Long sourceId;

    @Column(name = "project")
    @JsonProperty("project")
    private String project;

    @Column(name = "bucket")
    @JsonProperty("bucket")
    private String bucket;

    @Column(name = "file_name_pattern")
    @JsonProperty("file_name_pattern")
    private String fileNamePattern;

    @Column(name = "file_location_folder")
    @JsonProperty("file_location_folder")
    private String fileLocationFolder;

    @Column(name = "file_format")
    @JsonProperty("file_format")
    private String fileFormat;

    @Column(name = "file_frequency")
    @JsonProperty("file_frequency")
    private String fileFrequency;

    // Getters and setters

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }


    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    public String getFileLocationFolder() {
        return fileLocationFolder;
    }

    public void setFileLocationFolder(String fileLocationFolder) {
        this.fileLocationFolder = fileLocationFolder;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getFileFrequency() {
        return fileFrequency;
    }

    public void setFileFrequency(String fileFrequency) {
        this.fileFrequency = fileFrequency;
    }
}
