ALTER TABLE account
    ADD account_id UUID;

ALTER TABLE account
    ADD frozen_amount DECIMAL(19, 2);

ALTER TABLE account
    ADD status VARCHAR(255);

ALTER TABLE account
    ALTER COLUMN account_id SET NOT NULL;

ALTER TABLE account
    ALTER COLUMN frozen_amount SET NOT NULL;

ALTER TABLE account
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE transaction
    ADD status VARCHAR(255);

ALTER TABLE transaction
    ADD transaction_id UUID;

ALTER TABLE transaction
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE transaction
    ALTER COLUMN transaction_id SET NOT NULL;

ALTER TABLE account
    ADD CONSTRAINT uc_account_account UNIQUE (account_id);

ALTER TABLE transaction
    ADD CONSTRAINT uc_transaction_transaction UNIQUE (transaction_id);

ALTER TABLE account
    DROP COLUMN type;

ALTER TABLE account
    ADD type VARCHAR(255) NOT NULL;