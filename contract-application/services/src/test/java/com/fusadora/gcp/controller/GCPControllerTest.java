package com.fusadora.gcp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fusadora.gcp.service.GCPService;
import com.fusadora.model.datacontract.BigQueryDataset;
import com.fusadora.model.datacontract.GCPProjects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GCPController.class)
class GCPControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GCPService gcpService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveProject_shouldReturnSuccessMessage() throws Exception {
        // Given
        GCPProjects project = new GCPProjects();
        project.setGcpProjectId("test-project");

        doNothing().when(gcpService).addGCPProject(any(GCPProjects.class));

        // When & Then
        mockMvc.perform(post("/api/gcp/saveProject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(content().string("GCP Project saved successfully"));

        verify(gcpService, times(1)).addGCPProject(any(GCPProjects.class));
    }

    @Test
    void saveProject_shouldReturnErrorMessageOnException() throws Exception {
        // Given
        GCPProjects project = new GCPProjects();
        project.setGcpProjectId("test-project");

        doThrow(new RuntimeException("Database error")).when(gcpService).addGCPProject(any(GCPProjects.class));

        // When & Then
        mockMvc.perform(post("/api/gcp/saveProject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(content().string("Error saving GCP Project: Database error"));
    }

    @Test
    void getProjects_shouldReturnListOfProjects() throws Exception {
        // Given
        GCPProjects project1 = new GCPProjects();
        project1.setGcpProjectId("project1");

        GCPProjects project2 = new GCPProjects();
        project2.setGcpProjectId("project2");

        List<GCPProjects> projects = Arrays.asList(project1, project2);
        when(gcpService.getGCPProjects()).thenReturn(projects);

        // When & Then
        mockMvc.perform(get("/api/gcp/getProjects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].gcp_project_id").value("project1"))
                .andExpect(jsonPath("$[1].gcp_project_id").value("project2"));
    }

    @Test
    void getProjects_shouldReturnEmptyListWhenNoProjects() throws Exception {
        // Given
        when(gcpService.getGCPProjects()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/gcp/getProjects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBigQueryDatasets_shouldReturnDatasets() throws Exception {
        // Given
        String projectId = "test-project";
        BigQueryDataset dataset1 = new BigQueryDataset();
        dataset1.setProject(projectId);
        dataset1.setDataset("dataset1");

        BigQueryDataset dataset2 = new BigQueryDataset();
        dataset2.setProject(projectId);
        dataset2.setDataset("dataset2");

        List<BigQueryDataset> datasets = Arrays.asList(dataset1, dataset2);
        when(gcpService.getBigQueryDatasets(eq(projectId))).thenReturn(datasets);

        // When & Then
        mockMvc.perform(get("/api/gcp/getBigQueryDatasets/{gcpProjectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].project_id").value(projectId))
                .andExpect(jsonPath("$[0].data_set_name").value("dataset1"))
                .andExpect(jsonPath("$[1].data_set_name").value("dataset2"));
    }

    @Test
    void getBigQueryDatasets_shouldReturnEmptyListOnException() throws Exception {
        // Given
        String projectId = "test-project";
        when(gcpService.getBigQueryDatasets(eq(projectId))).thenThrow(new RuntimeException("BigQuery error"));

        // When & Then
        mockMvc.perform(get("/api/gcp/getBigQueryDatasets/{gcpProjectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBigQueryDatasets_shouldHandleSpecialCharactersInProjectId() throws Exception {
        // Given
        String projectId = "test-project-123";
        when(gcpService.getBigQueryDatasets(eq(projectId))).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/gcp/getBigQueryDatasets/{gcpProjectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(gcpService, times(1)).getBigQueryDatasets(eq(projectId));
    }
}