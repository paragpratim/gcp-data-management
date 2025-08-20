package org.fusadora.model.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@jakarta.persistence.Table(name = "physical_model")
public class PhysicalModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("physical_model_id")
    @Column(name = "physical_model_id", nullable = false, unique = true)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "physical_model_id")
    @JsonProperty("physicalTables")
    private List<PhysicalTable> physicalTables;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PhysicalTable> getPhysicalTables() {
        return physicalTables;
    }

    public void setPhysicalTables(List<PhysicalTable> physicalTables) {
        this.physicalTables = physicalTables;
    }
}
