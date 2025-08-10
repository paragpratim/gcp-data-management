package org.fusadora.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IngestionContract {
    @JsonProperty("contract_name")
    private String contractName;

    @JsonProperty("version")
    private String version;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("description")
    private String description;

    @JsonProperty("source")
    private Source source;

    @JsonProperty("target")
    private Target target;

    // Getters and setters
    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
}
