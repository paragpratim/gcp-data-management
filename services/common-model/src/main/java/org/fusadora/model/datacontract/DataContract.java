package org.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;

@Entity(name = "data_contract")
public class DataContract implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @JsonProperty("contract_id")
    @Field(name = "contract_id")
    private Long contractId;

    @JsonProperty("data_product_name")
    @Field(name = "data_product_name")
    private String dataProductName;

    @JsonProperty("version")
    @Field(name = "version")
    private String version;

    @JsonProperty("data_owner")
    @Field(name = "data_owner")
    private String dataOwner;

    @JsonProperty("description")
    @Field(name = "description")
    private String description;


    @JsonProperty("big_query_dataset")
    @Field(name = "big_query_dataset")
    private BigQueryDataset bigQueryDataset;

    @JsonProperty("physical_model")
    @Field(name = "physical_model")
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

    public BigQueryDataset getBigQueryDataset() {
        return bigQueryDataset;
    }

    public void setBigQueryDataset(BigQueryDataset bigQueryDataset) {
        this.bigQueryDataset = bigQueryDataset;
    }

    public PhysicalModel getPhysicalModel() {
        return physicalModel;
    }

    public void setPhysicalModel(PhysicalModel physicalModel) {
        this.physicalModel = physicalModel;
    }
}
