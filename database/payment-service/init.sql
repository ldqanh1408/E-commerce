-- =======================================================
-- PAYMENT SERVICE DATABASE INITIALIZATION
-- Database: payment_db
-- =======================================================

-- --- PHẦN 1: AXON FRAMEWORK TABLES ---

CREATE TABLE IF NOT EXISTS token_entry (
                                           processor_name VARCHAR(255) NOT NULL,
    segment INTEGER NOT NULL,
    token BYTEA,
    token_type VARCHAR(255),
    timestamp VARCHAR(255),
    owner VARCHAR(255),
    PRIMARY KEY (processor_name, segment)
    );

CREATE TABLE IF NOT EXISTS snapshot_event_entry (
                                                    aggregate_identifier VARCHAR(255) NOT NULL,
    sequence_number BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    event_identifier VARCHAR(255) NOT NULL,
    meta_data BYTEA,
    payload BYTEA NOT NULL,
    payload_revision VARCHAR(255),
    payload_type VARCHAR(255) NOT NULL,
    timestamp VARCHAR(255) NOT NULL,
    PRIMARY KEY (aggregate_identifier, sequence_number, type)
    );

-- --- PHẦN 2: BUSINESS TABLES (Read Model) ---

CREATE TABLE IF NOT EXISTS payments (
                                        id BIGSERIAL PRIMARY KEY,
                                        payment_id VARCHAR(255) UNIQUE NOT NULL, -- Axon Aggregate ID
    order_id VARCHAR(255) UNIQUE NOT NULL,   -- Link tới Order
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED
    payment_method VARCHAR(50),
    transaction_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );