package org.fusadora.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Source {

    @JsonProperty("project")
    private String project;

    @JsonProperty("bucket")
    private String bucket;

    @JsonProperty("file_name_pattern")
    private String fileNamePattern;

    @JsonProperty("file_location_folder")
    private String fileLocationFolder;

    @JsonProperty("file_format")
    private String fileFormat;

    @JsonProperty("file_frequency")
    private String fileFrequency;

    // Getters and setters
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
