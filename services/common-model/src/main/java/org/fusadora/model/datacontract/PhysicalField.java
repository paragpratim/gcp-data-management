package org.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity(name = "physical_field")
public class PhysicalField implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    @Field(name = "change_set_number")
    public int changeSetNumber;
    @JsonProperty("nested_fields")
    @Field(name = "nested_fields")
    private List<PhysicalField> nestedFields;
    @JsonProperty("field_name")
    @Field(name = "field_name")
    private String name;
    @JsonProperty("field_type")
    @Field(name = "field_type")
    private String type;
    @JsonProperty("field_description")
    @Field(name = "field_description")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PhysicalField> getNestedFields() {
        return nestedFields;
    }

    public void setNestedFields(List<PhysicalField> nestedFields) {
        this.nestedFields = nestedFields;
    }

    public int getChangeSetNumber() {
        return changeSetNumber;
    }

    public void setChangeSetNumber(int changeSetNumber) {
        this.changeSetNumber = changeSetNumber;
    }
}
