package org.fusadora.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "ingestion_contract")
public class IngestionContract implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("contract_id")
    @Column(name = "contract_id", nullable = false, unique = true)
    private Long contractId;

    @JsonProperty("contract_name")
    @Column(name = "contract_name", nullable = false)
    private String contractName;

    @JsonProperty("version")
    @Column(name = "version", nullable = false)
    private String version;

    @JsonProperty("owner")
    @Column(name = "owner", nullable = false)
    private String owner;

    @JsonProperty("description")
    @Column(name = "description")
    private String description;

    @JsonProperty("source")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id", referencedColumnName = "source_id")
    private Source source;

    @JsonProperty("target")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "target_id", referencedColumnName = "target_id")
    private Target target;

    // Getters and setters

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

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
