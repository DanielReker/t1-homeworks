-- 1. Clients
INSERT INTO client (id, first_name, last_name, middle_name, client_id) VALUES
(1, 'Alice', 'Smith', 'Mary', 'a1b2c3d4-e5f6-7890-1234-567890abcdef'),
(2, 'Bob', 'Johnson', NULL, 'b2c3d4e5-f6a7-8901-2345-67890abcdeff'),
(3, 'Carol', 'Williams', 'Jane', 'c3d4e5f6-a7b8-9012-3456-7890abcdef12'),
(4, 'David', 'Brown', 'Robert', 'd4e5f6a7-b8c9-0123-4567-890abcdef123'),
(5, 'Eve', 'Davis', NULL, 'e5f6a7b8-c9d0-1234-5678-90abcdef1234');

-- 2. Accounts
-- Client 1 Accounts
INSERT INTO account (id, account_id, client_id, type, status, balance, frozen_amount) VALUES
(1, 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 1, 'DEBIT', 'OPEN', 1500.75, 0.00),
(2, '55f3b116-f5cc-4a16-a6f1-53120e8cfadc', 1, 'CREDIT', 'OPEN', 5000.00, 100.50),
-- Client 2 Accounts
(3, '050bcb47-343f-4672-ae15-d610b9e99534', 2, 'DEBIT', 'OPEN', 250.00, 0.00),
-- Client 3 Accounts
(4, '0ace4605-eb41-4771-b8bf-d70332cdffff', 3, 'DEBIT', 'BLOCKED', 1200.00, 300.00),
(5, 'ef9493b6-67ee-46d3-8d7c-970fe3c1bccf', 3, 'DEBIT', 'OPEN', 0.00, 0.00),
-- Client 4 Accounts
(6, '94b1a69a-0ed2-455b-9a7f-fbf88e44926b', 4, 'CREDIT', 'ARRESTED', 7500.20, 7500.20),
-- Client 5 Accounts
(7, '9d764b04-c5e5-4559-b68e-d29ee240cb24', 5, 'DEBIT', 'OPEN', 999.99, 0.00),
(8, 'fa3cf168-6d73-40d7-9015-090b58229961', 5, 'CREDIT', 'CLOSED', 0.00, 0.00);


-- 3. Transactions
-- Transactions for Account 1 (Alice - DEBIT)
INSERT INTO transaction (id, transaction_id, status, account_id, amount, "time") VALUES
(1, 'bd4226f8-df6e-45e5-a992-fecdeec3b213', 'ACCEPTED', 1, 100.00, '2023-10-26T10:00:00Z'),
(2, '92c6fab6-1ea3-49eb-ae1e-b7ec322635d9', 'ACCEPTED', 1, -25.50, '2023-10-26T10:05:00Z'),
(3, 'fef007bc-8df0-415d-b226-a8119702981b', 'REQUESTED', 1, 50.00, '2023-10-27T09:15:00Z'),
(4, '6e2372b8-118d-41b7-81de-a5993909fd08', 'REJECTED', 1, -2000.00, '2023-10-27T11:00:00Z'), -- Insufficient funds potentially

-- Transactions for Account 2 (Alice - CREDIT)
(5, '7da50b78-3206-47a3-b09e-2ae41f858f7a', 'ACCEPTED', 2, -500.00, '2023-10-25T14:30:00Z'),
(6, '7cd1251d-43e5-4c9f-a809-5e26fc17e79a', 'ACCEPTED', 2, 200.00, '2023-10-26T16:00:00Z'),
(7, 'f121a3df-a862-403b-87a1-be1981c34018', 'CANCELLED', 2, -150.00, '2023-10-27T08:00:00Z'),

-- Transactions for Account 3 (Bob - DEBIT)
(8, '561cb192-231a-4906-96bc-6c19c079be00', 'ACCEPTED', 3, 30.00, '2023-10-27T12:00:00Z'),
(9, 'a65a7bcb-7f3f-4224-96f8-ff444e263dcd', 'ACCEPTED', 3, -10.00, '2023-10-27T12:05:00Z'),

-- Transactions for Account 4 (Carol - DEBIT, BLOCKED)
(10, '12aa7d98-6de2-4415-8ae4-c79acc8422a7', 'BLOCKED', 4, 75.00, '2023-10-24T10:00:00Z'), -- Account is blocked

-- Transactions for Account 6 (David - CREDIT, ARRESTED)
(11, '7acdf0b9-a1c5-460c-a183-858324ad80d1', 'BLOCKED', 6, -1000.00, '2023-10-23T11:30:00Z'), -- Account is arrested

-- Transactions for Account 7 (Eve - DEBIT)
(12, 'ba508c1c-b5e5-48f3-8dfc-baaf1438237c', 'ACCEPTED', 7, 50.25, '2023-10-20T10:00:00Z'),
(13, '7621d18d-3ded-441e-b410-4b6a4b499754', 'ACCEPTED', 7, 120.00, '2023-10-21T10:00:00Z'),
(14, '2e03686f-4987-4f50-9341-e25a05a5b9d3', 'ACCEPTED', 7, -33.10, '2023-10-22T10:00:00Z'),
(15, '7e93d109-e8a6-4d6d-9d68-25cc8f4d46f9', 'ACCEPTED', 7, -5.00, '2023-10-23T10:00:00Z');

-- Reset sequences
SELECT setval('client_id_seq', (SELECT MAX(id) FROM client));
SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('transaction_id_seq', (SELECT MAX(id) FROM transaction));