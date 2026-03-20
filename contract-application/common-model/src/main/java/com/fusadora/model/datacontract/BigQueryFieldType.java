package com.fusadora.model.datacontract;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * com.fusadora.model.datacontract.BigQueryFieldType
 * <p>Description: This enum represent list of supported BigQuery column data types for physical fields.</p>
 *
 * @author Parag Ghosh
 * @since 20/03/2026
 */
public enum BigQueryFieldType {
    STRING,
    BYTES,
    INT64,
    FLOAT64,
    NUMERIC,
    BIGNUMERIC,
    BOOL,
    DATE,
    DATETIME,
    TIME,
    TIMESTAMP,
    JSON,
    GEOGRAPHY,
    STRUCT,
    ARRAY;

    public static Optional<BigQueryFieldType> fromValue(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(type -> type.name().equals(normalized))
                .findFirst();
    }

    public static boolean isSupported(String value) {
        return fromValue(value).isPresent();
    }
}

