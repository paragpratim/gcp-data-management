package org.fusadora.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SourceTest {

    @Test
    void testSerializationAndDeserialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Create a sample Source object
        Source source = new Source();
        source.setProject("source-project");
        source.setBucket("source-bucket");
        source.setFileNamePattern("books_*.json");
        source.setFileLocationFolder("books/");
        source.setFileFormat("json");
        source.setFileFrequency("daily");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(source);
        assertNotNull(json);

        // Deserialize back to object
        Source deserializedSource = objectMapper.readValue(json, Source.class);
        assertNotNull(deserializedSource);
        assertEquals("source-project", deserializedSource.getProject());
        assertEquals("source-bucket", deserializedSource.getBucket());
        assertEquals("books_*.json", deserializedSource.getFileNamePattern());
        assertEquals("books/", deserializedSource.getFileLocationFolder());
        assertEquals("json", deserializedSource.getFileFormat());
        assertEquals("daily", deserializedSource.getFileFrequency());
    }
}
