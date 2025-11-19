package com.fusadora.contract.utils;

import com.fusadora.model.datacontract.DataContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DataContractVersionUtilTest {

    private DataContract dataContract;
    private static final Long DATA_CONTRACT_ID = Long.parseLong("123456789");

    @BeforeEach
    void setUp() {
        dataContract = new DataContract();
    }

    @Test
    void constructor_shouldThrowException() {
        // When & Then
        assertThrows(IllegalStateException.class, DataContractVersionUtil::new);
    }

    @ParameterizedTest
    @MethodSource("addNextVersionTestCases")
    void addNextVersion_parameterizedTest(Long contractId, Double inputVersion, Double expectedVersion, String description) {
        // Given
        dataContract.setContractId(contractId);
        dataContract.setVersion(inputVersion);

        // When
        DataContract result = DataContractVersionUtil.addNextVersion(dataContract);

        // Then
        assertNotNull(result);
        assertEquals(expectedVersion, result.getVersion(), 0.001, description);
        assertSame(dataContract, result);
    }

    private static Stream<Arguments> addNextVersionTestCases() {
        return Stream.of(
                Arguments.of(null, null, 0.1d, "New contract with null id and version"),
                Arguments.of(null, 1.0, 0.1d, "Null contract id with existing version"),
                Arguments.of(DATA_CONTRACT_ID, null, 0.1d, "Null version with existing contract id"),
                Arguments.of(DATA_CONTRACT_ID, 1.0, 1.1d, "Normal version increment"),
                Arguments.of(DATA_CONTRACT_ID, 0.1, 0.2d, "Increment from initial version"),
                Arguments.of(DATA_CONTRACT_ID, 2.5, 2.6d, "Multiple increments"),
                Arguments.of(DATA_CONTRACT_ID, 99.9, 100.0d, "Large version numbers"),
                Arguments.of(DATA_CONTRACT_ID, 0.9, 1.0d, "Floating point precision"),
                Arguments.of(DATA_CONTRACT_ID, 0.0, 0.1d, "Zero version"),
                Arguments.of(DATA_CONTRACT_ID, 1.15, 1.3d, "Rounding to one decimal place"),
                Arguments.of(0L, 1.0, 1.1d, "Zero contract id"),
                Arguments.of(DATA_CONTRACT_ID, -1.0, -0.9d, "Negative version")
        );
    }

    @Test
    void addNextVersion_shouldModifyOriginalContract() {
        // Given
        dataContract.setContractId(DATA_CONTRACT_ID);
        dataContract.setVersion(1.0);
        Double originalVersion = dataContract.getVersion();

        // When
        DataContract result = DataContractVersionUtil.addNextVersion(dataContract);

        // Then
        assertSame(dataContract, result);
        assertNotEquals(originalVersion, dataContract.getVersion());
        assertEquals(1.1d, dataContract.getVersion());
    }
}
