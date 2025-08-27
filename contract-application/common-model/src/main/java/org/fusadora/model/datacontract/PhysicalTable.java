package org.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity(name = "physical_table")
public class PhysicalTable implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("table_name")
    @Field(name = "table_name")
    private String name;

    @JsonProperty("table_description")
    @Field(name = "table_description")
    private String description;

    @JsonProperty("physical_fields")
    @Field(name = "physical_fields")
    private List<PhysicalField> physicalFields;

    @JsonProperty("partitioning_fields")
    @Field(name = "partitioning_fields")
    private String partitioningFields;

    @JsonProperty("clustering_fields")
    @Field(name = "clustering_fields")
    private String clusteringFields;

    @JsonIgnore
    @Field(name = "current_changeset_number")
    private int currentChangeSetNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PhysicalField> getPhysicalFields() {
        return physicalFields;
    }

    public void setPhysicalFields(List<PhysicalField> physicalFields) {
        this.physicalFields = physicalFields;
    }

    public String getPartitioningFields() {
        return partitioningFields;
    }

    public void setPartitioningFields(String partitioningFields) {
        this.partitioningFields = partitioningFields;
    }

    public String getClusteringFields() {
        return clusteringFields;
    }

    public void setClusteringFields(String clusteringFields) {
        this.clusteringFields = clusteringFields;
    }

    public int getCurrentChangeSetNumber() {
        return currentChangeSetNumber;
    }

    public void setCurrentChangeSetNumber(int currentChangeSetNumber) {
        this.currentChangeSetNumber = currentChangeSetNumber;
    }
}
