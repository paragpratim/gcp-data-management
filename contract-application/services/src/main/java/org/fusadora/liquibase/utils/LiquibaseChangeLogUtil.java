package org.fusadora.liquibase.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LiquibaseChangeLogUtil {

    private LiquibaseChangeLogUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void generateLiquibaseChangeLogJsonFile(String aProjectId, String aDataSetName, String liquibasePath, String dataProductVersion) throws IOException {
        String jsonContent = getLiquibaseChangeLogJson(aDataSetName);
        // File Path: <liquibasePath>/<projectId>/<dataProductVersion>/<dataSetName>.json
        Path liquibaseDirectoryPath = Path.of(liquibasePath).resolve(aProjectId).resolve(dataProductVersion);
        Files.createDirectories(liquibaseDirectoryPath);
        Files.writeString(liquibaseDirectoryPath.resolve(aDataSetName + ".json"), jsonContent);
    }

    public static String getLiquibaseChangeLogJson(String aDataSetName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        ObjectNode include = mapper.createObjectNode();
        include.put("path", aDataSetName + File.separator);
        include.put("relativeToChangelogFile", true);

        ObjectNode includeWrapper = mapper.createObjectNode();
        includeWrapper.set("includeAll", include);

        ArrayNode changeLogList = mapper.createArrayNode();
        changeLogList.add(includeWrapper);

        root.set("databaseChangeLog", changeLogList);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }
}
