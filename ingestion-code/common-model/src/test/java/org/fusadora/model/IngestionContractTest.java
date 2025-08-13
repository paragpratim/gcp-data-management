package org.fusadora.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngestionContractTest {

    @Test
    void testSerializationAndDeserialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Create a sample IngestionContract object
        IngestionContract contract = getIngestionContract();

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(contract);
        assertNotNull(json);

        // Deserialize back to object
        IngestionContract deserializedContract = objectMapper.readValue(json, IngestionContract.class);
        assertNotNull(deserializedContract);
        assertEquals("sample_books_ingestion", deserializedContract.getContractName());
        assertEquals("1.0", deserializedContract.getVersion());
        assertEquals("sample-owner", deserializedContract.getOwner());
        assertEquals("Data contract for ingesting books data.", deserializedContract.getDescription());
        assertEquals("source-project", deserializedContract.getSource().getProject());
        assertEquals("target-project", deserializedContract.getTarget().getProject());
    }

    private static IngestionContract getIngestionContract() {
        Source source = new Source();
        source.setProject("source-project");
        source.setBucket("source-bucket");
        source.setFileNamePattern("books_*.json");
        source.setFileLocationFolder("books/");
        source.setFileFormat("json");
        source.setFileFrequency("daily");

        Target target = new Target();
        target.setProject("target-project");
        target.setBigqueryDataset("target-dataset");
        target.setBigqueryTable("target-table");

        IngestionContract contract = new IngestionContract();
        contract.setContractName("sample_books_ingestion");
        contract.setVersion("1.0");
        contract.setOwner("sample-owner");
        contract.setDescription("Data contract for ingesting books data.");
        contract.setSource(source);
        contract.setTarget(target);
        return contract;
    }
}