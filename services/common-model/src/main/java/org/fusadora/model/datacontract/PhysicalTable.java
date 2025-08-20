package org.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@jakarta.persistence.Table(name = "tables")
public class PhysicalTable implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("table_id")
    @Column(name = "table_id", nullable = false, unique = true)
    private Long id;

    @JsonProperty("table_name")
    @Column(name = "table_name", nullable = false)
    private String name;

    @JsonProperty("Method_description")
    @Column(name = "Method_description")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id")
    @JsonProperty("fields")
    private List<Field> fieldlist;

    @JsonProperty("partitioning_fields")
    @Column(name = "partitioning_fields")
    private String partitioningFields;

    @JsonProperty("clustering_fields")
    @Column(name = "clustering_fields")
    private String clusteringFields;

    @ManyToOne
    @JoinColumn(name = "physical_model_id", nullable = false)
    @JsonProperty("physical_model")
    private PhysicalModel physicalModel;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<Field> getFieldlist() {
        return fieldlist;
    }

    public void setFieldlist(List<Field> fieldlist) {
        this.fieldlist = fieldlist;
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

    public PhysicalModel getPhysicalModel() {
        return physicalModel;
    }

    public void setPhysicalModel(PhysicalModel physicalModel) {
        this.physicalModel = physicalModel;
    }

}
