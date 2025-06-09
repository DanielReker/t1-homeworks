ALTER TABLE transaction
    ADD account_id UUID;

ALTER TABLE transaction
    ALTER COLUMN account_id SET NOT NULL;