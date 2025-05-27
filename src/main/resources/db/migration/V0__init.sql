CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE account
(
    id        BIGINT         NOT NULL,
    client_id BIGINT         NOT NULL UNIQUE,
    type      SMALLINT       NOT NULL,
    balance   DECIMAL(19, 2) NOT NULL,
    CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE TABLE client
(
    id          BIGINT       NOT NULL,
    first_name  VARCHAR(255) NOT NULL,
    last_name   VARCHAR(255) NOT NULL,
    middle_name VARCHAR(255) NOT NULL,
    client_id   UUID         NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (id)
);

CREATE TABLE data_source_error_log
(
    id               BIGINT NOT NULL,
    stacktrace       VARCHAR(255),
    message          VARCHAR(255),
    method_signature VARCHAR(255),
    CONSTRAINT pk_data_source_error_log PRIMARY KEY (id)
);

CREATE TABLE transaction
(
    id         BIGINT                   NOT NULL,
    account_id BIGINT                   NOT NULL,
    amount     DECIMAL(19, 2)           NOT NULL,
    time       TIMESTAMP with time zone NOT NULL,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

ALTER TABLE client
    ADD CONSTRAINT UC_CLIENT_CLIENT UNIQUE (client_id);

ALTER TABLE account
    ADD CONSTRAINT FK_ACCOUNT_ON_CLIENT FOREIGN KEY (client_id) REFERENCES client (id);

ALTER TABLE transaction
    ADD CONSTRAINT FK_TRANSACTION_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);