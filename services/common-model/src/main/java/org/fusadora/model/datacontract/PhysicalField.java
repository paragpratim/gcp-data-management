package org.fusadora.model.datacontract;

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

    @JsonProperty("nested_fields")
    @Field(name = "nested_fields")
    List<PhysicalField> nestedFields;

    @JsonProperty("field_name")
    @Field(name = "field_name")
    private String name;

    @JsonProperty("field_type")
    @Field(name = "field_type")
    private String type;

    @JsonProperty("field_description")
    @Field(name = "field_description")
    private String description;

    @JsonProperty("is_primary_key")
    @Field(name = "is_primary_key")
    private boolean isPrimaryKey;

    @JsonProperty("is_foreign_key")
    @Field(name = "is_foreign_key")
    private boolean isForeignKey;

    @JsonProperty("foreign_key_table")
    @Field(name = "foreign_key_table")
    private String foreignKeyTable;

    @JsonProperty("foreign_key_field")
    @Field(name = "foreign_key_field")
    private String foreignKeyField;

    @JsonProperty("constraints")
    @Field(name = "constraints")
    private String constraints;

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

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isForeignKey() {
        return isForeignKey;
    }

    public void setForeignKey(boolean foreignKey) {
        isForeignKey = foreignKey;
    }

    public String getForeignKeyTable() {
        return foreignKeyTable;
    }

    public void setForeignKeyTable(String foreignKeyTable) {
        this.foreignKeyTable = foreignKeyTable;
    }

    public String getForeignKeyField() {
        return foreignKeyField;
    }

    public void setForeignKeyField(String foreignKeyField) {
        this.foreignKeyField = foreignKeyField;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public List<PhysicalField> getNestedFields() {
        return nestedFields;
    }

    public void setNestedFields(List<PhysicalField> nestedFields) {
        this.nestedFields = nestedFields;
    }
}
