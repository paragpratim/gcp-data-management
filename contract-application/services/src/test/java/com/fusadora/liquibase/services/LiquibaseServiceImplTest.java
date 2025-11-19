package com.fusadora.liquibase.services;

import com.fusadora.liquibase.repository.LiquibaseRepository;
import com.fusadora.liquibase.utils.LiquibaseChangeLogUtil;
import com.fusadora.liquibase.utils.LiquibaseChangeSetUtil;
import com.fusadora.liquibase.utils.LiquibaseCommandUtil;
import com.fusadora.model.datacontract.BigQueryDataset;
import com.fusadora.model.datacontract.DataContract;
import com.fusadora.model.datacontract.PhysicalModel;
import com.fusadora.model.datacontract.PhysicalTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LiquibaseServiceImplTest {

    @Mock
    private LiquibaseRepository liquibaseRepository;

    @InjectMocks
    private LiquibaseServiceImpl liquibaseService;

    private DataContract dataContract;
    private PhysicalModel physicalModel;
    private PhysicalTable physicalTable1;
    private PhysicalTable physicalTable2;

    @BeforeEach
    void setUp() {
        // Set up properties using ReflectionTestUtils
        ReflectionTestUtils.setField(liquibaseService, "liquibaseChangePath", "/mnt/liquibase");
        ReflectionTestUtils.setField(liquibaseService, "liquibaseChangeDataset", "liquibase_admin");

        // Set up test data
        BigQueryDataset bigQueryDataset = new BigQueryDataset();
        bigQueryDataset.setProject("test-project");
        bigQueryDataset.setDataset("test-dataset");

        physicalTable1 = new PhysicalTable();
        physicalTable1.setName("table1");

        physicalTable2 = new PhysicalTable();
        physicalTable2.setName("table2");

        physicalModel = new PhysicalModel();
        physicalModel.setPhysicalTables(List.of(physicalTable1, physicalTable2));

        dataContract = new DataContract();
        dataContract.setContractId(123L);
        dataContract.setVersion(1.0);
        dataContract.setBigQueryDataset(bigQueryDataset);
        dataContract.setPhysicalModel(physicalModel);
    }

    @Test
    void generateChangeLog_shouldGenerateSuccessfully() throws IOException {
        // Given
        String contractId = "123";
        String expectedPath = "/expected/path";
        when(liquibaseRepository.findById(123L)).thenReturn(Optional.of(dataContract));

        try (MockedStatic<LiquibaseChangeLogUtil> changeLogUtil = mockStatic(LiquibaseChangeLogUtil.class);
             MockedStatic<LiquibaseChangeSetUtil> changeSetUtil = mockStatic(LiquibaseChangeSetUtil.class)) {

            when(LiquibaseChangeLogUtil.generateLiquibaseChangeLogJsonFile(
                    "test-project", "test-dataset", "/mnt/liquibase/123", "1.0"))
                    .thenReturn(expectedPath);

            // When
            String result = liquibaseService.generateChangeLog(contractId);

            // Then
            assertEquals(expectedPath, result);
            verify(liquibaseRepository).findById(123L);
            changeLogUtil.verify(() -> LiquibaseChangeLogUtil.generateLiquibaseChangeLogJsonFile(
                    "test-project", "test-dataset", "/mnt/liquibase/123", "1.0"));
            changeSetUtil.verify(() -> LiquibaseChangeSetUtil.generateLiquibaseChangeSetSqlFile(
                    physicalTable1, "test-project", "test-dataset", "/mnt/liquibase/123", "1.0"));
            changeSetUtil.verify(() -> LiquibaseChangeSetUtil.generateLiquibaseChangeSetSqlFile(
                    physicalTable2, "test-project", "test-dataset", "/mnt/liquibase/123", "1.0"));
        }
    }

    @Test
    void generateChangeLog_shouldThrowExceptionWhenContractNotFound() {
        // Given
        String contractId = "999";
        when(liquibaseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> liquibaseService.generateChangeLog(contractId));
        assertEquals("Contract not found with id: 999", exception.getMessage());
        verify(liquibaseRepository).findById(999L);
    }

    @Test
    void generateChangeLog_shouldThrowExceptionForInvalidContractId() {
        // Given
        String invalidContractId = "invalid";

        // When & Then
        assertThrows(NumberFormatException.class,
                () -> liquibaseService.generateChangeLog(invalidContractId));
        verifyNoInteractions(liquibaseRepository);
    }

    @Test
    void generateChangeLog_shouldHandleChangeLogGenerationException() throws IOException {
        // Given
        String contractId = "123";
        when(liquibaseRepository.findById(123L)).thenReturn(Optional.of(dataContract));

        try (MockedStatic<LiquibaseChangeLogUtil> changeLogUtil = mockStatic(LiquibaseChangeLogUtil.class);
             MockedStatic<LiquibaseChangeSetUtil> changeSetUtil = mockStatic(LiquibaseChangeSetUtil.class)) {

            when(LiquibaseChangeLogUtil.generateLiquibaseChangeLogJsonFile(
                    anyString(), anyString(), anyString(), anyString()))
                    .thenThrow(new IOException("Change log generation failed"));

            // When
            String result = liquibaseService.generateChangeLog(contractId);

            // Then
            assertNull(result);
            verify(liquibaseRepository).findById(123L);
            // Verify that change set generation is still attempted despite change log failure
            changeSetUtil.verify(() -> LiquibaseChangeSetUtil.generateLiquibaseChangeSetSqlFile(
                    physicalTable1, "test-project", "test-dataset", "/mnt/liquibase/123", "1.0"));
        }
    }

    @Test
    void generateChangeLog_shouldHandleChangeSetGenerationException() throws IOException {
        // Given
        String contractId = "123";
        String expectedPath = "/expected/path";
        when(liquibaseRepository.findById(123L)).thenReturn(Optional.of(dataContract));

        try (MockedStatic<LiquibaseChangeLogUtil> changeLogUtil = mockStatic(LiquibaseChangeLogUtil.class);
             MockedStatic<LiquibaseChangeSetUtil> changeSetUtil = mockStatic(LiquibaseChangeSetUtil.class)) {

            when(LiquibaseChangeLogUtil.generateLiquibaseChangeLogJsonFile(
                    anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(expectedPath);

            doThrow(new IOException("Change set generation failed"))
                    .when(LiquibaseChangeSetUtil.class);
            LiquibaseChangeSetUtil.generateLiquibaseChangeSetSqlFile(
                    any(PhysicalTable.class), anyString(), anyString(), anyString(), anyString());

            // When
            String result = liquibaseService.generateChangeLog(contractId);

            // Then
            assertEquals(expectedPath, result);
            verify(liquibaseRepository).findById(123L);
        }
    }

    @Test
    void applyChangeLog_shouldApplySuccessfully() {
        // Given
        String contractId = "123";
        String expectedResponse = "Liquibase update completed successfully";
        when(liquibaseRepository.findById(123L)).thenReturn(Optional.of(dataContract));

        try (MockedStatic<LiquibaseCommandUtil> commandUtil = mockStatic(LiquibaseCommandUtil.class)) {
            when(LiquibaseCommandUtil.updateBigQuery(
                    "test-project", "test-dataset", "/mnt/liquibase/123", "1.0", "123", "liquibase_admin"))
                    .thenReturn(expectedResponse);

            // When
            String result = liquibaseService.applyChangeLog(contractId);

            // Then
            assertEquals(expectedResponse, result);
            verify(liquibaseRepository).findById(123L);
            commandUtil.verify(() -> LiquibaseCommandUtil.updateBigQuery(
                    "test-project", "test-dataset", "/mnt/liquibase/123", "1.0", "123", "liquibase_admin"));
        }
    }

    @Test
    void applyChangeLog_shouldThrowExceptionWhenContractNotFound() {
        // Given
        String contractId = "999";
        when(liquibaseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> liquibaseService.applyChangeLog(contractId));
        assertEquals("Contract not found with id: 999", exception.getMessage());
        verify(liquibaseRepository).findById(999L);
    }

    @Test
    void applyChangeLog_shouldThrowExceptionForInvalidContractId() {
        // Given
        String invalidContractId = "invalid";

        // When & Then
        assertThrows(NumberFormatException.class,
                () -> liquibaseService.applyChangeLog(invalidContractId));
        verifyNoInteractions(liquibaseRepository);
    }

    @Test
    void liquibasePathFor_shouldConstructCorrectPath() {
        // Given
        String contractId = "123";

        // When
        String result = liquibaseService.liquibasePathFor(contractId);

        // Then
        assertEquals("/mnt/liquibase/123", result);
    }

    @Test
    void liquibasePathFor_shouldHandleDifferentContractIds() {
        // Test with different contract IDs
        assertEquals("/mnt/liquibase/456", liquibaseService.liquibasePathFor("456"));
        assertEquals("/mnt/liquibase/0", liquibaseService.liquibasePathFor("0"));
        assertEquals("/mnt/liquibase/999999", liquibaseService.liquibasePathFor("999999"));
    }

    @Test
    void liquibasePathFor_shouldUseConfiguredBasePath() {
        // Given
        ReflectionTestUtils.setField(liquibaseService, "liquibaseChangePath", "/custom/path");
        String contractId = "123";

        // When
        String result = liquibaseService.liquibasePathFor(contractId);

        // Then
        assertEquals("/custom/path/123", result);
    }

    @Test
    void generateChangeLog_shouldHandleEmptyPhysicalTables() throws IOException {
        // Given
        String contractId = "123";
        String expectedPath = "/expected/path";
        physicalModel.setPhysicalTables(List.of()); // Empty list
        when(liquibaseRepository.findById(123L)).thenReturn(Optional.of(dataContract));

        try (MockedStatic<LiquibaseChangeLogUtil> changeLogUtil = mockStatic(LiquibaseChangeLogUtil.class);
             MockedStatic<LiquibaseChangeSetUtil> changeSetUtil = mockStatic(LiquibaseChangeSetUtil.class)) {

            when(LiquibaseChangeLogUtil.generateLiquibaseChangeLogJsonFile(
                    anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(expectedPath);

            // When
            String result = liquibaseService.generateChangeLog(contractId);

            // Then
            assertEquals(expectedPath, result);
            verify(liquibaseRepository).findById(123L);
            changeLogUtil.verify(() -> LiquibaseChangeLogUtil.generateLiquibaseChangeLogJsonFile(
                    "test-project", "test-dataset", "/mnt/liquibase/123", "1.0"));
            // Verify no change set generation calls since no tables
            changeSetUtil.verifyNoInteractions();
        }
    }

    @Test
    void generateChangeLog_shouldHandleNullPhysicalTables() throws IOException {
        // Given
        String contractId = "123";
        String expectedPath = "/expected/path";
        physicalModel.setPhysicalTables(null); // Null list
        when(liquibaseRepository.findById(123L)).thenReturn(Optional.of(dataContract));

        try (MockedStatic<LiquibaseChangeLogUtil> changeLogUtil = mockStatic(LiquibaseChangeLogUtil.class)) {
            when(LiquibaseChangeLogUtil.generateLiquibaseChangeLogJsonFile(
                    anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(expectedPath);

            // When & Then
            assertThrows(NullPointerException.class, () -> liquibaseService.generateChangeLog(contractId));
        }
    }
}
