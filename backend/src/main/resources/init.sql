CREATE TABLE IF NOT EXISTS transactions (
                                            id UUID PRIMARY KEY,
                                            amount DOUBLE PRECISION NOT NULL,
                                            currency VARCHAR(3) NOT NULL,
    description VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
    );