CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    entity_type TEXT,
    person_type TEXT,
    operation_date TIMESTAMP NOT NULL,
    transaction_type TEXT,
    comment VARCHAR(500),
    amount DOUBLE PRECISION NOT NULL,
    status TEXT,
    sender_bank TEXT,
    account TEXT,
    receiver_bank TEXT,
    receiver_inn VARCHAR(12),
    receiver_account TEXT,
    category TEXT,
    receiver_phone VARCHAR(20),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
    );