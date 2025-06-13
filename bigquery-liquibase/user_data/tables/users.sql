--liquibase formatted sql

--changeset users-data-owner:users_1
CREATE TABLE IF NOT EXISTS user_data.users (
    registration_dttm TIMESTAMP,
    id INT64,
    first_name STRING,
    last_name STRING,
    email STRING,
    gender STRING,
    ip_address STRING,
    cc STRING,
    country STRING,
    birthdate STRING,
    salary DECIMAL(10, 2),
    title STRING,
    comment STRING
);
--rollback DROP TABLE IF EXISTS user_data.users;
