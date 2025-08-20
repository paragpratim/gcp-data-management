package org.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "data_contract")
public class DataContract implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("contract_id")
    @Column(name = "contract_id", nullable = false, unique = true)
    private Long contractId;

    @JsonProperty("data_product_name")
    @Column(name = "data_product_name", nullable = false)
    private String dataProductName;

    @JsonProperty("version")
    @Column(name = "version", nullable = false)
    private String version;

    @JsonProperty("data_owner")
    @Column(name = "data_owner", nullable = false)
    private String dataOwner;

    @JsonProperty("description")
    @Column(name = "description")
    private String description;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "output_port_id", referencedColumnName = "output_port_id")
    @JsonProperty("output_port")
    private OutPutPort outputPort;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "physical_model_id", referencedColumnName = "physical_model_id")
    @JsonProperty("physical_model")
    private PhysicalModel physicalModel;

    // Getters and setters

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getDataProductName() {
        return dataProductName;
    }

    public void setDataProductName(String dataProductName) {
        this.dataProductName = dataProductName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDataOwner() {
        return dataOwner;
    }

    public void setDataOwner(String dataOwner) {
        this.dataOwner = dataOwner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OutPutPort getOutputPort() {
        return outputPort;
    }

    public void setOutputPort(OutPutPort outputPort) {
        this.outputPort = outputPort;
    }

    public PhysicalModel getPhysicalModel() {
        return physicalModel;
    }

    public void setPhysicalModel(PhysicalModel physicalModel) {
        this.physicalModel = physicalModel;
    }
}
