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

--changeset inventory-data-owner:colors_2
ALTER TABLE inventory_data.colors
ADD COLUMN IF NOT EXISTS description STRING;
--rollback ALTER TABLE inventory_data.colors DROP COLUMN IF EXISTS description;

--changeset inventory-data-owner:colors_3
ALTER TABLE inventory_data.colors
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP OPTIONS (description="The timestamp when the color was created");
--rollback ALTER TABLE inventory_data.colors DROP COLUMN IF EXISTS created_at;

