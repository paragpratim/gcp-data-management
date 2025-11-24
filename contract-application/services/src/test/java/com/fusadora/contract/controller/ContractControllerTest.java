package com.fusadora.contract.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fusadora.contract.service.ContractService;
import com.fusadora.model.datacontract.DataContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContractController.class)
@ExtendWith(MockitoExtension.class)
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractService contractService;

    @Autowired
    private ObjectMapper objectMapper;

    private DataContract contract;

    @BeforeEach
    void setUp() {
        contract = new DataContract();
        contract.setContractId(1L);
        contract.setDataProductName("Test Contract");
    }

    @Test
    void saveContract_shouldReturnSuccessMessage() throws Exception {
        // Given
        doNothing().when(contractService).saveContract(any(DataContract.class));

        // When & Then
        mockMvc.perform(post("/api/contracts/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contract)))
                .andExpect(status().isOk())
                .andExpect(content().string("Contract [1] saved successfully"));

        verify(contractService).saveContract(any(DataContract.class));
    }

    @Test
    void saveContract_shouldReturnErrorMessageWhenExceptionOccurs() throws Exception {
        // Given
        doThrow(new RuntimeException("Save failed")).when(contractService).saveContract(any(DataContract.class));

        // When & Then
        mockMvc.perform(post("/api/contracts/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contract)))
                .andExpect(status().isOk())
                .andExpect(content().string("Error saving contract: Save failed"));
    }

    @Test
    void getContract_shouldReturnContract() throws Exception {
        // Given
        String contractId = "1";
        when(contractService.getContract(contractId)).thenReturn(contract);

        // When & Then
        mockMvc.perform(get("/api/contracts/get/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract_id").value(1))
                .andExpect(jsonPath("$.data_product_name").value("Test Contract"));

        verify(contractService).getContract(contractId);
    }

    @Test
    void deleteContract_shouldReturnSuccessMessage() throws Exception {
        // Given
        String contractId = "1";
        doNothing().when(contractService).deleteContract(contractId);

        // When & Then
        mockMvc.perform(delete("/api/contracts/delete/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(content().string("Contract deleted successfully"));

        verify(contractService).deleteContract(contractId);
    }

    @Test
    void deleteContract_shouldReturnErrorMessageWhenExceptionOccurs() throws Exception {
        // Given
        String contractId = "1";
        doThrow(new RuntimeException("Delete failed")).when(contractService).deleteContract(contractId);

        // When & Then
        mockMvc.perform(delete("/api/contracts/delete/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(content().string("Error deleting contract: Delete failed"));
    }

    @Test
    void updateContract_shouldReturnSuccessMessage() throws Exception {
        // Given
        doNothing().when(contractService).updateContract(any(DataContract.class));

        // When & Then
        mockMvc.perform(put("/api/contracts/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contract)))
                .andExpect(status().isOk())
                .andExpect(content().string("Contract [1] updated successfully"));

        verify(contractService).updateContract(any(DataContract.class));
    }

    @Test
    void updateContract_shouldReturnErrorMessageWhenExceptionOccurs() throws Exception {
        // Given
        doThrow(new RuntimeException("Update failed")).when(contractService).updateContract(any(DataContract.class));

        // When & Then
        mockMvc.perform(put("/api/contracts/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contract)))
                .andExpect(status().isOk())
                .andExpect(content().string("Error updating contract: Update failed"));
    }

    @Test
    void contractExists_shouldReturnTrue() throws Exception {
        // Given
        String contractId = "1";
        when(contractService.contractExists(contractId)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/contracts/exists/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(contractService).contractExists(contractId);
    }

    @Test
    void contractExists_shouldReturnFalse() throws Exception {
        // Given
        String contractId = "999";
        when(contractService.contractExists(contractId)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/contracts/exists/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(contractService).contractExists(contractId);
    }

    @Test
    void getAllContractIds_shouldReturnListOfIds() throws Exception {
        // Given
        List<Long> contractIds = List.of(1L, 2L, 3L);
        when(contractService.getAllContractIds()).thenReturn(contractIds);

        // When & Then
        mockMvc.perform(get("/api/contracts/all-ids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2))
                .andExpect(jsonPath("$[2]").value(3));

        verify(contractService).getAllContractIds();
    }

    @Test
    void getAllContractIds_shouldReturnEmptyList() throws Exception {
        // Given
        when(contractService.getAllContractIds()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/contracts/all-ids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(contractService).getAllContractIds();
    }

    @Test
    void getAllContracts_shouldReturnListOfContracts() throws Exception {
        // Given
        DataContract contract2 = new DataContract();
        contract2.setContractId(2L);
        contract2.setDataProductName("Test Contract 2");

        List<DataContract> contracts = List.of(contract, contract2);
        when(contractService.getAllContracts()).thenReturn(contracts);

        // When & Then
        mockMvc.perform(get("/api/contracts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].contract_id").value(1))
                .andExpect(jsonPath("$[0].data_product_name").value("Test Contract"))
                .andExpect(jsonPath("$[1].contract_id").value(2))
                .andExpect(jsonPath("$[1].data_product_name").value("Test Contract 2"));

        verify(contractService).getAllContracts();
    }

    @Test
    void getAllContracts_shouldReturnEmptyList() throws Exception {
        // Given
        when(contractService.getAllContracts()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/contracts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(contractService).getAllContracts();
    }

    @Test
    void validateContract_shouldReturnSuccessMessage() throws Exception {
        // Given
        doNothing().when(contractService).validateContract(any(DataContract.class));

        // When & Then
        mockMvc.perform(post("/api/contracts/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contract)))
                .andExpect(status().isOk())
                .andExpect(content().string("Contract is valid"));

        verify(contractService).validateContract(any(DataContract.class));
    }

    @Test
    void validateContract_shouldReturnErrorMessageWhenValidationFails() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Validation failed")).when(contractService).validateContract(any(DataContract.class));

        // When & Then
        mockMvc.perform(post("/api/contracts/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contract)))
                .andExpect(status().isOk())
                .andExpect(content().string("Contract validation failed: Validation failed"));
    }

    @Test
    void saveContract_shouldHandleInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/contracts/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    @Test
    void updateContract_shouldHandleInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/contracts/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    @Test
    void validateContract_shouldHandleInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/contracts/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }
}
