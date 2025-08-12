package org.fusadora.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TargetTest {

    @Test
    void testSerializationAndDeserialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Create a sample Target object
        Target target = new Target();
        target.setProject("target-project");
        target.setBigqueryDataset("target-dataset");
        target.setBigqueryTable("target-table");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(target);
        assertNotNull(json);

        // Deserialize back to object
        Target deserializedTarget = objectMapper.readValue(json, Target.class);
        assertNotNull(deserializedTarget);
        assertEquals("target-project", deserializedTarget.getProject());
        assertEquals("target-dataset", deserializedTarget.getBigqueryDataset());
        assertEquals("target-table", deserializedTarget.getBigqueryTable());
    }
}
