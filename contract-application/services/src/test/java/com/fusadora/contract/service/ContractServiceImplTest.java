package com.fusadora.contract.service;

import com.fusadora.contract.repository.ContractRepository;
import com.fusadora.contract.utils.DataContractChangeSetUtil;
import com.fusadora.contract.utils.DataContractVersionUtil;
import com.fusadora.model.datacontract.DataContract;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private ContractServiceImpl contractService;

    private DataContract contract;

    @BeforeEach
    void setUp() {
        contract = new DataContract();
        contract.setContractId(1L);
        contract.setDataProductName("Test Contract");
    }

    @Test
    void saveContract_shouldSaveValidContract() {
        // Given
        try (MockedStatic<DataContractChangeSetUtil> changeSetUtil = mockStatic(DataContractChangeSetUtil.class);
             MockedStatic<DataContractVersionUtil> versionUtil = mockStatic(DataContractVersionUtil.class)) {

            when(DataContractVersionUtil.addNextVersion(contract)).thenReturn(contract);

            // When
            contractService.saveContract(contract);

            // Then
            changeSetUtil.verify(() -> DataContractChangeSetUtil.addChangeSetNumber(contract));
            versionUtil.verify(() -> DataContractVersionUtil.addNextVersion(contract));
            verify(contractRepository).save(contract);
        }
    }

    @Test
    void saveContract_shouldThrowExceptionWhenContractIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.saveContract(null));
        assertEquals("Contract cannot be null", exception.getMessage());
        verifyNoInteractions(contractRepository);
    }

    @Test
    void getContract_shouldReturnContractWhenFound() {
        // Given
        String contractId = "1";
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));

        // When
        DataContract result = contractService.getContract(contractId);

        // Then
        assertNotNull(result);
        assertEquals(contract, result);
        verify(contractRepository).findById(1L);
    }

    @Test
    void getContract_shouldThrowExceptionWhenContractNotFound() {
        // Given
        String contractId = "999";
        when(contractRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.getContract(contractId));
        assertEquals("Contract not found with id: 999", exception.getMessage());
    }

    @Test
    void getContract_shouldThrowExceptionForInvalidContractIdFormat() {
        // Given
        String invalidContractId = "invalid";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.getContract(invalidContractId));
        assertEquals("Invalid contract ID format: invalid", exception.getMessage());
        assertInstanceOf(NumberFormatException.class, exception.getCause());
        verifyNoInteractions(contractRepository);
    }

    @Test
    void deleteContract_shouldDeleteExistingContract() {
        // Given
        String contractId = "1";
        when(contractRepository.existsById(1L)).thenReturn(true);

        // When
        contractService.deleteContract(contractId);

        // Then
        verify(contractRepository).existsById(1L);
        verify(contractRepository).deleteById(1L);
    }

    @Test
    void deleteContract_shouldThrowExceptionWhenContractNotFound() {
        // Given
        String contractId = "999";
        when(contractRepository.existsById(999L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.deleteContract(contractId));
        assertEquals("Contract not found with id: 999", exception.getMessage());
        verify(contractRepository).existsById(999L);
        verify(contractRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteContract_shouldThrowExceptionForInvalidContractIdFormat() {
        // Given
        String invalidContractId = "invalid";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.deleteContract(invalidContractId));
        assertEquals("Invalid contract ID format: invalid", exception.getMessage());
        assertInstanceOf(NumberFormatException.class, exception.getCause());
        verifyNoInteractions(contractRepository);
    }

    @Test
    void updateContract_shouldUpdateExistingContract() {
        // Given
        when(contractRepository.existsById(1L)).thenReturn(true);
        try (MockedStatic<DataContractChangeSetUtil> changeSetUtil = mockStatic(DataContractChangeSetUtil.class);
             MockedStatic<DataContractVersionUtil> versionUtil = mockStatic(DataContractVersionUtil.class)) {

            when(DataContractVersionUtil.addNextVersion(contract)).thenReturn(contract);

            // When
            contractService.updateContract(contract);

            // Then
            verify(contractRepository).existsById(1L);
            changeSetUtil.verify(() -> DataContractChangeSetUtil.updateChangeSetNumber(contract));
            versionUtil.verify(() -> DataContractVersionUtil.addNextVersion(contract));
            verify(contractRepository).save(contract);
        }
    }

    @Test
    void updateContract_shouldThrowExceptionWhenContractNotFound() {
        // Given
        when(contractRepository.existsById(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.updateContract(contract));
        assertEquals("Contract not found with id: 1", exception.getMessage());
        verify(contractRepository).existsById(1L);
        verify(contractRepository, never()).save(any());
    }

    @Test
    void contractExists_shouldReturnTrueWhenContractExists() {
        // Given
        String contractId = "1";
        when(contractRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = contractService.contractExists(contractId);

        // Then
        assertTrue(result);
        verify(contractRepository).existsById(1L);
    }

    @Test
    void contractExists_shouldReturnFalseWhenContractDoesNotExist() {
        // Given
        String contractId = "999";
        when(contractRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = contractService.contractExists(contractId);

        // Then
        assertFalse(result);
        verify(contractRepository).existsById(999L);
    }

    @Test
    void contractExists_shouldThrowExceptionForInvalidContractIdFormat() {
        // Given
        String invalidContractId = "invalid";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.contractExists(invalidContractId));
        assertEquals("Invalid contract ID format: invalid", exception.getMessage());
        assertInstanceOf(NumberFormatException.class, exception.getCause());
        verifyNoInteractions(contractRepository);
    }

    @Test
    void validateContract_shouldPassValidContract() {
        // Given
        when(validator.validate(contract)).thenReturn(Set.of());

        // When & Then
        assertDoesNotThrow(() -> contractService.validateContract(contract));
        verify(validator).validate(contract);
    }

    @Test
    void validateContract_shouldThrowExceptionWhenContractIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.validateContract(null));
        assertEquals("Contract cannot be null", exception.getMessage());
        verifyNoInteractions(validator);
    }

    @Test
    void validateContract_shouldThrowExceptionWhenDataProductNameIsNull() {
        // Given
        contract.setDataProductName(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.validateContract(contract));
        assertEquals("Contract name cannot be null or empty", exception.getMessage());
        verifyNoInteractions(validator);
    }

    @Test
    void validateContract_shouldThrowExceptionWhenDataProductNameIsEmpty() {
        // Given
        contract.setDataProductName("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.validateContract(contract));
        assertEquals("Contract name cannot be null or empty", exception.getMessage());
        verifyNoInteractions(validator);
    }

    @Test
    void validateContract_shouldThrowExceptionWhenValidationFails() {
        // Given
        ConstraintViolation<DataContract> violation = mock();
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getPropertyPath().toString()).thenReturn("field");
        when(violation.getMessage()).thenReturn("is required");
        when(validator.validate(contract)).thenReturn(Set.of(violation));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contractService.validateContract(contract));
        assertTrue(exception.getMessage().contains("Contract validation failed"));
        assertTrue(exception.getMessage().contains("field: is required"));
        verify(validator).validate(contract);
    }

    @Test
    void getAllContractIds_shouldReturnAllContractIds() {
        // Given
        DataContract contract1 = new DataContract();
        contract1.setContractId(1L);
        DataContract contract2 = new DataContract();
        contract2.setContractId(2L);

        List<DataContract> contracts = List.of(contract1, contract2);
        when(contractRepository.findAll()).thenReturn(contracts);

        // When
        List<Long> result = contractService.getAllContractIds();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        verify(contractRepository).findAll();
    }

    @Test
    void getAllContractIds_shouldReturnEmptyListWhenNoContracts() {
        // Given
        when(contractRepository.findAll()).thenReturn(List.of());

        // When
        List<Long> result = contractService.getAllContractIds();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contractRepository).findAll();
    }

    @Test
    void getAllContracts_shouldReturnAllContracts() {
        // Given
        DataContract contract1 = new DataContract();
        contract1.setContractId(1L);
        DataContract contract2 = new DataContract();
        contract2.setContractId(2L);

        List<DataContract> contracts = List.of(contract1, contract2);
        when(contractRepository.findAll()).thenReturn(contracts);

        // When
        List<DataContract> result = contractService.getAllContracts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(contracts, result);
        verify(contractRepository).findAll();
    }

    @Test
    void getAllContracts_shouldReturnEmptyListWhenNoContracts() {
        // Given
        when(contractRepository.findAll()).thenReturn(List.of());

        // When
        List<DataContract> result = contractService.getAllContracts();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contractRepository).findAll();
    }
}
