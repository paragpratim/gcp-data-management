package com.fusadora.gcp.service;

import com.fusadora.gcp.repository.GCPRepository;
import com.fusadora.model.datacontract.BigQueryDataset;
import com.fusadora.model.datacontract.GCPProjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GCPServiceImplTest {

    @Mock
    private GCPRepository gcpRepository;

    @InjectMocks
    private GCPServiceImpl gcpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(gcpService, "liquibaseChangeDataset", "liquibase_admin");
    }

    @Test
    void addGCPProject_shouldSaveProject() {
        // Given
        GCPProjects project = new GCPProjects();
        project.setGcpProjectId("test-project");

        // When
        gcpService.addGCPProject(project);

        // Then
        verify(gcpRepository, times(1)).save(project);
    }

    @Test
    void getGCPProjects_shouldReturnAllProjects() {
        // Given
        GCPProjects project1 = new GCPProjects();
        project1.setGcpProjectId("project1");
        GCPProjects project2 = new GCPProjects();
        project2.setGcpProjectId("project2");

        when(gcpRepository.findAll()).thenReturn(Arrays.asList(project1, project2));

        // When
        List<GCPProjects> result = gcpService.getGCPProjects();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("project1", result.get(0).getGcpProjectId());
        assertEquals("project2", result.get(1).getGcpProjectId());
    }

    @Test
    void getBigQueryDatasets_withDummyProject_shouldReturnDummyDataset() {
        // When
        List<BigQueryDataset> result = gcpService.getBigQueryDatasets("dummy-project");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("dummy-project", result.get(0).getProject());
        assertEquals("dummy-dataset", result.get(0).getDataset());
    }

    @Test
    void getBigQueryDatasets_withNonDummyProject_shouldHandleBigQueryException() {
        // When
        List<BigQueryDataset> result = gcpService.getBigQueryDatasets("non-dummy-project");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteGCPProject_shouldNotThrowException() {
        // When/Then
        assertDoesNotThrow(() -> gcpService.deleteGCPProject("test-project"));
    }
}