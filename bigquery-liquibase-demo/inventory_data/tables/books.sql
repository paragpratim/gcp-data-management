--liquibase formatted sql

--changeset inventory-data-owner:books_1
CREATE TABLE IF NOT EXISTS inventory_data.books (
    id STRING,
    cat STRING,
    name STRING,
    author STRING,
    series_t STRING,
    sequence_i INT64,
    genre_s STRING,
    inStock BOOL,
    price DECIMAL(10, 2),
    pages_i INT64
);
--rollback DROP TABLE IF EXISTS inventory_data.books;
