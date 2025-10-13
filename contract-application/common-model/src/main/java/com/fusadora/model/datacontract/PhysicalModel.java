package com.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity(name = "physical_model")
public class PhysicalModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @Field(name = "physical_tables")
    @JsonProperty("physical_tables")
    private List<PhysicalTable> physicalTables;

    public List<PhysicalTable> getPhysicalTables() {
        return physicalTables;
    }

    public void setPhysicalTables(List<PhysicalTable> physicalTables) {
        this.physicalTables = physicalTables;
    }
}
