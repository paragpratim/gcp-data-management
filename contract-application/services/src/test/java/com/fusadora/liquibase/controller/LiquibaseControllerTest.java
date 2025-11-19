package com.fusadora.liquibase.controller;

import com.fusadora.liquibase.services.LiquibaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LiquibaseController.class)
@ExtendWith(MockitoExtension.class)
class LiquibaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LiquibaseService liquibaseService;

    private String contractId;

    @BeforeEach
    void setUp() {
        contractId = "123";
    }

    @Test
    void generateChangelog_shouldReturnSuccessMessage() throws Exception {
        // Given
        String expectedResponse = "/mnt/liquibase/123/changelog.json";
        when(liquibaseService.generateChangeLog(contractId)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/liquibase/generateChangelog/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(liquibaseService).generateChangeLog(contractId);
    }

    @Test
    void generateChangelog_shouldHandleServiceException() throws Exception {
        // Given
        when(liquibaseService.generateChangeLog(contractId))
                .thenThrow(new RuntimeException("Generation failed"));

        // When & Then
        mockMvc.perform(post("/api/liquibase/generateChangelog/{contractId}", contractId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error: Generation failed"));

        verify(liquibaseService).generateChangeLog(contractId);
    }

    @Test
    void generateChangelog_shouldHandleIllegalArgumentException() throws Exception {
        // Given
        when(liquibaseService.generateChangeLog(contractId))
                .thenThrow(new IllegalArgumentException("Contract not found"));

        // When & Then
        mockMvc.perform(post("/api/liquibase/generateChangelog/{contractId}", contractId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request: Contract not found"));

        verify(liquibaseService).generateChangeLog(contractId);
    }

    @Test
    void generateChangelog_shouldHandleInvalidContractId() throws Exception {
        // Given
        String invalidContractId = "invalid";
        when(liquibaseService.generateChangeLog(invalidContractId))
                .thenThrow(new NumberFormatException("Invalid contract ID"));

        // When & Then
        mockMvc.perform(post("/api/liquibase/generateChangelog/{contractId}", invalidContractId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid contract ID format: Invalid contract ID"));

        verify(liquibaseService).generateChangeLog(invalidContractId);
    }

    @Test
    void generateChangelog_shouldHandleNullResponse() throws Exception {
        // Given
        when(liquibaseService.generateChangeLog(contractId)).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/liquibase/generateChangelog/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(liquibaseService).generateChangeLog(contractId);
    }

    @Test
    void applyChangelog_shouldReturnSuccessMessage() throws Exception {
        // Given
        String expectedResponse = "Liquibase update completed successfully";
        when(liquibaseService.applyChangeLog(contractId)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/liquibase/applyChangelog/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(liquibaseService).applyChangeLog(contractId);
    }

    @Test
    void applyChangelog_shouldHandleServiceException() throws Exception {
        // Given
        when(liquibaseService.applyChangeLog(contractId))
                .thenThrow(new RuntimeException("Apply failed"));

        // When & Then
        mockMvc.perform(post("/api/liquibase/applyChangelog/{contractId}", contractId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error: Apply failed"));

        verify(liquibaseService).applyChangeLog(contractId);
    }

    @Test
    void applyChangelog_shouldHandleIllegalArgumentException() throws Exception {
        // Given
        when(liquibaseService.applyChangeLog(contractId))
                .thenThrow(new IllegalArgumentException("Contract not found"));

        // When & Then
        mockMvc.perform(post("/api/liquibase/applyChangelog/{contractId}", contractId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request: Contract not found"));

        verify(liquibaseService).applyChangeLog(contractId);
    }

    @Test
    void applyChangelog_shouldHandleInvalidContractId() throws Exception {
        // Given
        String invalidContractId = "invalid";
        when(liquibaseService.applyChangeLog(invalidContractId))
                .thenThrow(new NumberFormatException("Invalid contract ID"));

        // When & Then
        mockMvc.perform(post("/api/liquibase/applyChangelog/{contractId}", invalidContractId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid contract ID format: Invalid contract ID"));

        verify(liquibaseService).applyChangeLog(invalidContractId);
    }

    @Test
    void applyChangelog_shouldHandleNullResponse() throws Exception {
        // Given
        when(liquibaseService.applyChangeLog(contractId)).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/liquibase/applyChangelog/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(liquibaseService).applyChangeLog(contractId);
    }

    @Test
    void generateChangelog_shouldAcceptNumericContractId() throws Exception {
        // Given
        String numericContractId = "999";
        String expectedResponse = "/mnt/liquibase/999/changelog.json";
        when(liquibaseService.generateChangeLog(numericContractId)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/liquibase/generateChangelog/{contractId}", numericContractId))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(liquibaseService).generateChangeLog(numericContractId);
    }

    @Test
    void applyChangelog_shouldAcceptNumericContractId() throws Exception {
        // Given
        String numericContractId = "999";
        String expectedResponse = "Liquibase update completed for contract 999";
        when(liquibaseService.applyChangeLog(numericContractId)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/liquibase/applyChangelog/{contractId}", numericContractId))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(liquibaseService).applyChangeLog(numericContractId);
    }

    @Test
    void generateChangelog_shouldHandleSpecialCharactersInContractId() throws Exception {
        // Given
        String specialContractId = "test-123_contract";
        when(liquibaseService.generateChangeLog(specialContractId))
                .thenThrow(new NumberFormatException("Invalid format"));

        // When & Then
        mockMvc.perform(post("/api/liquibase/generateChangelog/{contractId}", specialContractId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid contract ID format: Invalid format"));

        verify(liquibaseService).generateChangeLog(specialContractId);
    }

    @Test
    void applyChangelog_shouldHandleSpecialCharactersInContractId() throws Exception {
        // Given
        String specialContractId = "test-123_contract";
        when(liquibaseService.applyChangeLog(specialContractId))
                .thenThrow(new NumberFormatException("Invalid format"));

        // When & Then
        mockMvc.perform(post("/api/liquibase/applyChangelog/{contractId}", specialContractId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid contract ID format: Invalid format"));

        verify(liquibaseService).applyChangeLog(specialContractId);
    }

    @Test
    void controller_shouldHaveCrossOriginAnnotation() throws Exception {
        // This test verifies that CORS is properly configured
        // Given
        String expectedResponse = "success";
        when(liquibaseService.generateChangeLog(contractId)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/liquibase/generateChangelog/{contractId}", contractId)
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}
