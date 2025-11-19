package com.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fusadora.model.validator.ValidDataContract;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;

/**
 * com.fusadora.model.datacontract.DataContract
 * This class represents a data contract entity for managing data products.
 * It includes validation annotations to ensure that required fields are not blank and that nested objects are valid.
 * The class is mapped to a Datastore entity named "data_contract".
 * @author Parag Ghosh
 * @since 16/11/2025
 */

@Entity(name = "data_contract")
@ValidDataContract
public class DataContract implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @JsonProperty("contract_id")
    @Field(name = "contract_id")
    private Long contractId;

    @NotBlank(message = "Data product name is required")
    @JsonProperty("data_product_name")
    @Field(name = "data_product_name")
    private String dataProductName;

    @JsonProperty("version")
    @Field(name = "version")
    private Double version;

    @NotBlank(message = "Data owner is required")
    @JsonProperty("data_owner")
    @Field(name = "data_owner")
    private String dataOwner;

    @NotBlank(message = "Description is required")
    @JsonProperty("description")
    @Field(name = "description")
    private String description;

    @Valid
    @JsonProperty("big_query_dataset")
    @Field(name = "big_query_dataset")
    private BigQueryDataset bigQueryDataset;

    @Valid
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

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
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
