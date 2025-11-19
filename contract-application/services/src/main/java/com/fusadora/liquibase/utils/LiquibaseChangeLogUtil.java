package com.fusadora.liquibase.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * com.fusadora.liquibase.utils.LiquibaseChangeLogUtil
 * Utility class for generating Liquibase changelog JSON files.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

public class LiquibaseChangeLogUtil {

    private LiquibaseChangeLogUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a Liquibase changelog JSON file for the specified dataset.
     *
     * @param aProjectId        The project ID.
     * @param aDataSetName      The dataset name.
     * @param liquibasePath     The base path for Liquibase files.
     * @param dataProductVersion The version of the data product.
     * @return The path to the generated Liquibase changelog JSON file.
     * @throws IOException If an I/O error occurs.
     */
    public static String generateLiquibaseChangeLogJsonFile(String aProjectId, String aDataSetName, String liquibasePath, String dataProductVersion) throws IOException {
        String jsonContent = getLiquibaseChangeLogJson(aDataSetName);
        // File Path: <liquibasePath>/<projectId>/<dataProductVersion>/<dataSetName>.json
        Path liquibaseDirectoryPath = Path.of(liquibasePath).resolve(aProjectId).resolve(dataProductVersion);
        Files.createDirectories(liquibaseDirectoryPath);
        Files.writeString(liquibaseDirectoryPath.resolve(aDataSetName + ".json"), jsonContent);
        return liquibaseDirectoryPath.toString();
    }

    /**
     * Constructs the JSON content for a Liquibase changelog file.
     *
     * @param aDataSetName The dataset name.
     * @return The JSON content as a string.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
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
