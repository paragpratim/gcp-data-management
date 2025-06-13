--liquibase formatted sql

--changeset inventory-data-owner:colors_1
CREATE TABLE IF NOT EXISTS inventory_data.colors (
    color STRING,
    category STRING,
    type STRING,
    code STRUCT <
      rgba ARRAY <STRING>,
      hex STRING
      >,
    other STRUCT <
      type STRING
      >
);
--rollback DROP TABLE IF EXISTS inventory_data.colors;
