package org.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "table_fields")
public class Field implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("field_id")
    @Column(name = "field_id", nullable = false, unique = true)
    private Long id;

    @JsonProperty("field_name")
    @Column(name = "field_name", nullable = false)
    private String name;

    @JsonProperty("field_type")
    @Column(name = "field_type", nullable = false)
    private String type;

    @JsonProperty("field_description")
    @Column(name = "field_description")
    private String description;

    @JsonProperty("is_primary_key")
    @Column(name = "is_primary_key", nullable = false)
    private boolean isPrimaryKey;

    @JsonProperty("is_foreign_key")
    @Column(name = "is_foreign_key", nullable = false)
    private boolean isForeignKey;

    @JsonProperty("foreign_key_table")
    @Column(name = "foreign_key_table")
    private String foreignKeyTable;

    @JsonProperty("foreign_key_field")
    @Column(name = "foreign_key_field")
    private String foreignKeyField;

    @JsonProperty("constraints")
    @Column(name = "constraints")
    private String constraints;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    @JsonProperty("table")
    private PhysicalTable table;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PhysicalTable getTable() {
        return table;
    }

    public void setTable(PhysicalTable table) {
        this.table = table;
    }
}
